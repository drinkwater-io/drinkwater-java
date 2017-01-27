package drinkwater.core;

import drinkwater.*;
import drinkwater.core.helper.BeanFactory;
import drinkwater.core.helper.DefaultPropertyResolver;
import drinkwater.core.helper.Service;
import drinkwater.core.internal.*;
import drinkwater.core.reflect.BeanClassInvocationHandler;
import drinkwater.core.reflect.BeanInvocationHandler;
import drinkwater.helper.reflect.ReflectHelper;
import drinkwater.rest.RestInvocationHandler;
import drinkwater.rest.RestService;
import drinkwater.trace.ConsoleEventLogger;
import drinkwater.trace.EventAggregator;
import javaslang.collection.List;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.util.StopWatch;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.enterprise.inject.Vetoed;

/**
 * Created by A406775 on 27/12/2016.
 */
//@Vetoed
public class DrinkWaterApplication implements ServiceRepository {

    public static final String DW_STATICHANDLER = "dw-static-management-handler";
    public static final String DW_STATIC_WWW_HANDLER = "dw-static-www-handler";
    private static Logger logger = Logger.getLogger(DrinkWaterApplication.class.getName());

    static {
        //FIXME manage the logging system
        java.util.logging.Logger topLogger = java.util.logging.Logger.getLogger("");
        topLogger.setLevel(Level.INFO);
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
    }

    private List<DrinkWaterApplicationHistory> applicationHistory = List.empty();
    private java.util.List<IDataStore> dataStores = new ArrayList<>();
    private ApplicationState state = ApplicationState.Stopped;
    private String name;
    private TracerBean tracer;
    private JVMMetricsBean jvmMetricsBean;
    private RestService restConfiguration;
    private List<IDrinkWaterService> services;
    private Map<String, Object> serviceProxies;
    private Service managementService;
    private boolean useServiceManagement = false;
    private EventAggregator eventAggregator = new EventAggregator();
    private boolean isTraceMaster = true;
    private Class eventLoggerClass = null;
    //fixme : should support multiple service builder
    private ServiceConfigurationBuilder serviceBuilders;
    private Properties initialApplicationProperties = new Properties();

    private CamelContext applicationLevelContext;

    private IBaseEventLogger currentBaseEventLogger;


    private DrinkWaterApplication() {
        this(null);
    }

    private DrinkWaterApplication(String name) {
        if (name == null) {
            name = "drinkwater";
        }
        this.name = name;
    }

    private DrinkWaterApplication(String name, boolean useServiceManagement) {
        this(name, useServiceManagement, true);
    }

    private DrinkWaterApplication(String name, boolean useServiceManagement, boolean isTraceMaster) {
        this(name);
        this.useServiceManagement = useServiceManagement;
        this.isTraceMaster = isTraceMaster;
    }

    public static DrinkWaterApplication create() {
        return new DrinkWaterApplication();
    }

    public static DrinkWaterApplication create(String name) {
        return new DrinkWaterApplication(name);
    }

    public static DrinkWaterApplication create(String name, boolean useServiceManagement) {
        return new DrinkWaterApplication(name, useServiceManagement);
    }

    public static DrinkWaterApplication create(String name, boolean useServiceManagement, boolean traceMaster) {
        return new DrinkWaterApplication(name, useServiceManagement, traceMaster);
    }

    public static RouteBuilder createCoreRoutes(String managementHost, DrinkWaterApplication app) {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                from(String.format(
                        "jetty:http://%s?handlers=%s", managementHost, DW_STATICHANDLER))
                        .to("mock:empty?retainFirst=1");

                from(String.format(
                        "jetty:http://%s?handlers=%s", "localhost:8380", DW_STATIC_WWW_HANDLER))
                        .to("mock:empty?retainFirst=1");

                from(String.format(
                        "jetty:http://%s/stopApplication?httpMethodRestrict=POST",managementHost))
                        .bean(new ShutDownDrinkWaterBean(app)).id("app_shutdown");


            }
        };
    }

    private static ResourceHandler getManagementResourceHandler() {
        ResourceHandler staticHandler = new ResourceHandler();
        staticHandler.setBaseResource(Resource.newClassPathResource("/www/drinkwater/management"));
        return staticHandler;
    }

    private static ResourceHandler getWWWResourceHandler() {
        ResourceHandler staticHandler = new ResourceHandler();
        staticHandler.setBaseResource(Resource.newClassPathResource("/www"));
        return staticHandler;
    }

    private RouteBuilder createTracingRoute(EventAggregator aggregator, IBaseEventLogger logger) {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                from("vm:trace").bean(logger, "logEvent(${body})").bean(aggregator, "addEvent(${body})");

            }
        };
    }

    public synchronized void startTracingRoute() {
        try {
            if (isTraceMaster) {

                //get logger class
                Class loggerImplementationClass = eventLoggerClass == null ? ConsoleEventLogger.class : eventLoggerClass;
                ServiceConfiguration traceBeanConfig = new ServiceConfiguration();
                traceBeanConfig.setServiceName(this.getName() + "." + loggerImplementationClass.getSimpleName());
                traceBeanConfig.setServiceClass(IBaseEventLogger.class);
                //traceBeanConfig.setTargetBean(loggerImplementationClass.newInstance());
                traceBeanConfig.setTargetBeanClass(loggerImplementationClass);
                traceBeanConfig.setScheme(ServiceScheme.BeanClass);
                traceBeanConfig.useTracing(false);
                initialApplicationProperties.forEach((key, value) -> traceBeanConfig.withProperty(key.toString(), value.toString()));

                Service tracingService = createServiceFromConfig(traceBeanConfig, tracer);

                currentBaseEventLogger = (IBaseEventLogger) BeanFactory.createBean(this, tracingService.getConfiguration(), tracingService);

                applicationLevelContext.addRoutes(createTracingRoute(eventAggregator, currentBaseEventLogger));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public IBaseEventLogger getCurrentBaseEventLogger() {
        return currentBaseEventLogger;
    }

    public synchronized void startApplicationContext() {

        try {
            applicationLevelContext = CamelContextFactory.createCamelContext("applicationlevelContext");
            applicationLevelContext.getShutdownStrategy().setTimeout(1);
            applicationLevelContext.getShutdownStrategy().setTimeUnit(TimeUnit.NANOSECONDS);
            applicationLevelContext.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void stopApplicationContext() {
        try {
            logger.info(String.format("Shutting down application context for  %s .... goodbye !!!", this.getName()));
            applicationLevelContext.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void cleanBeforeStart() {
        if (serviceProxies != null) {
            serviceProxies.clear();
        }
        serviceProxies = new HashMap<>();
        services = List.empty();
        tracer = new TracerBean();
        jvmMetricsBean = new JVMMetricsBean();
        restConfiguration = new RestService();
        if (isTraceMaster) {
            eventAggregator.clear();
        }
//        eventAggregator = new EventAggregator();
    }

    public ServiceConfigurationBuilder configuration() {
        return serviceBuilders;
    }

    public TracerBean getTracer() {
        return tracer;
    }

    public void addServiceBuilder(ServiceConfigurationBuilder builder) {

        this.serviceBuilders = builder;

//        builder.configure();
//
//        List.ofAll(builder.getConfigurations()).forEach(this::addService);
    }

    private void cofigureServices() {
        //initialize stores


        if(serviceBuilders != null){
            serviceBuilders.configure();
            dataStores = serviceBuilders.getDataStores();
            List.ofAll(serviceBuilders.getConfigurations()).forEach(this::addService);
        }
        else{
            //TODO add explanation how to ad a srvice
            logger.warning("no service builder initialized, add at leas one service");
        }

    }

    public boolean isStarted() {
        return this.state == ApplicationState.Up;
    }

    public boolean isStopped() {
        return this.state == ApplicationState.Stopped;
    }

    //fixme should throw error if called twice
    public void start() {


        if (isStarted()) {
            return;
        }


        startApplicationContext();

        logStartInfo();

        cleanBeforeStart();

        startExternalServices();

        cofigureServices();

        startDataStores();

        startTracingRoute();

        for (IDrinkWaterService service : services) {
            service.start();
        }

        if (useServiceManagement) {
            createAndStartManagementService();
        }

        logStartedInfo();

        state = ApplicationState.Up;
    }

    public void stop() {

        StopWatch timingWath = new StopWatch();
        try {


            if (isStopped()) {
                return;
            }

            logStopingInfo();

            for (IDrinkWaterService service : services) {
                service.stop();
            }

            stopExternalServices();

            if (useServiceManagement) {
                stopManagementService();
            }

            stopDataStores();

            stopApplicationContext();

            state = ApplicationState.Stopped;
            timingWath.stop();
        }
        finally {
            logStoppedInfo(timingWath);
        }

    }

    private void startDataStores() {
        for (IDataStore store :
                dataStores) {

            try {
                store.start();
                store.migrate();
            } catch (Exception e) {
                throw new RuntimeException("could not start or migrate datastore", e);
            }

        }
    }

    private void stopDataStores() {
        for (IDataStore store :
                dataStores) {

            try {
                store.close();
            } catch (Exception e) {
                throw new RuntimeException("could not close datastore", e);
            }

        }
    }

    private void addService(IServiceConfiguration serviceConfig) {
        Service s = createServiceFromConfig(serviceConfig, tracer);
        services = services.append(s);
        addProxy(s);
    }

    private Service createServiceFromConfig(IServiceConfiguration serviceConfig, ITracer metricTracer) {
        try {

            Service service = new Service(serviceConfig, metricTracer, this);

            service.configure(this);

            return service;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void addProxy(Service serviceToProxy) {
        if (serviceToProxy.getConfiguration().getScheme() == ServiceScheme.BeanObject) {
            serviceProxies.put(serviceToProxy.getConfiguration().getServiceName(),
                    ReflectHelper.simpleProxy(serviceToProxy.getConfiguration().getServiceClass(),
                            new BeanInvocationHandler(serviceToProxy.getCamelContext(), this, serviceToProxy)));
        } else if (serviceToProxy.getConfiguration().getScheme() == ServiceScheme.BeanClass) {
            serviceProxies.put(serviceToProxy.getConfiguration().getServiceName(),
                    ReflectHelper.simpleProxy(serviceToProxy.getConfiguration().getServiceClass(),
                            new BeanClassInvocationHandler(serviceToProxy.getCamelContext())));
        } else if (serviceToProxy.getConfiguration().getScheme() == ServiceScheme.Rest ||
                serviceToProxy.getConfiguration().getScheme() == ServiceScheme.Remote) {
            serviceProxies.put(serviceToProxy.getConfiguration().getServiceName(),
                    ReflectHelper.simpleProxy(serviceToProxy.getConfiguration().getServiceClass(),
                            new RestInvocationHandler(new DefaultPropertyResolver(serviceToProxy), serviceToProxy)));
        }
    }

    //TODO : it could be that more than one implements a service class
    @Override
    public <T> T getService(Class<? extends T> iface) {
        T service = null;
        //check in service if there is one that serves iface
        if(services.filter(s -> s.getConfiguration().getServiceClass().equals(iface)).size() > 0){
            service = (T) serviceProxies.get(services.filter(s -> s.getConfiguration().getServiceClass().equals(iface)).get().getConfiguration().getServiceName());
        }
        if(service == null){
            service = (T) serviceProxies.get(iface.getName());
        }
        return service;
    }

    @Override
    public <T> T getService(String serviceName) {
        return (T) serviceProxies.get(serviceName);
    }

    @Override
    public IServiceConfiguration getServiceDefinition(String serviceName) {
        return
                services.filter(s -> s.getConfiguration().getServiceName().equals(serviceName))
                        .map(s -> s.getConfiguration())
                        .get();
    }

    private void startExternalServices() {
        restConfiguration.start();
    }

    private void stopExternalServices() {
        restConfiguration.start();
    }

    private void createAndStartManagementService() {

        try {

            logger.info("starting management service");

            ServiceManagementBean serviceManagement = new ServiceManagementBean(
                    this,
                    jvmMetricsBean.getMetrics());

            String defaultPropertiesName = "classpath:" + this.name + ".properties";

            logger.info(String.format("getting management service properties from file : %s", defaultPropertiesName));

            //FIXME : use another way to initialize the servicemanagement bean
            ServiceConfiguration config =
                    (ServiceConfiguration) new ServiceConfiguration()
                            .forService(IServiceManagement.class)
                            .useBean(serviceManagement)
                            .withProperties(defaultPropertiesName)
                            .withInjectionStrategy(InjectionStrategy.None)
                            .name(this.name)
                            .asRest();

            managementService = createServiceFromConfig(config, tracer);

            CamelContextFactory.registerBean(applicationLevelContext, DW_STATICHANDLER, getManagementResourceHandler());

            CamelContextFactory.registerBean(applicationLevelContext, DW_STATIC_WWW_HANDLER, getWWWResourceHandler());

            String managementHost = managementService.lookupProperty(name + ".management.host:0.0.0.0");
            String managementPort = managementService.lookupProperty(name + ".management.port:9000");
            String managementHostAndPort = managementHost + ":" + managementPort;

            applicationLevelContext.addRoutes(createCoreRoutes(managementHostAndPort, this));

            logger.info("management web page can be found here : http://" + managementHostAndPort);


            managementService.start();

            logger.info("management service started");
        } catch (Exception ex) {
            throw new RuntimeException("Could not start Core Context ", ex);
        }
    }



    private void stopManagementService() {
        if (managementService != null) {

            managementService.stop();
        }
    }

    private void logStartInfo() {
        logger.info("-----------------------STARTING " + name + "------------------------------------");
    }

    private void logStartedInfo() {
        logger.info("----------------------- " + name + " STARTED------------------------------------");
    }

    private void logStopingInfo() {
        logger.info("-----------------------STOPING " + name + "------------------------------------");
    }

    private void logStoppedInfo(StopWatch stoppedwatch) {
        logger.info(String.format("application stopped in %d ms", stoppedwatch.taken()));
        logger.info("have a nice day");
        logger.info("-----------------------STOPPED " + name + "------------------------------------");
    }

    //fixme : it should be possible to restart only some services...
    public void patchService(String serviceName, Object beanObject) {

        this.stop();

        ServiceConfigurationBuilder newBuilder = new ServiceConfigurationBuilder(serviceBuilders.getConfigurations());
        IServiceConfiguration config = newBuilder.getConfiguration(serviceName);
        config.setScheme(ServiceScheme.BeanObject);
        config.setInjectionStrategy(InjectionStrategy.None);
        config.setTargetBean(beanObject);

        this.serviceBuilders = newBuilder;

        this.start();
    }

    public void patchService(String serviceName, IServiceConfiguration patchConfig) {

        this.stop();

        ServiceConfigurationBuilder newBuilder = new ServiceConfigurationBuilder(serviceBuilders.getConfigurations());
        IServiceConfiguration config = newBuilder.getConfiguration(serviceName);
        config.patchWith(patchConfig);

        this.serviceBuilders = newBuilder;

        this.start();
    }

    public synchronized DrinkWaterApplicationHistory takeSnapShot() {
//        this.stop();

        DrinkWaterApplicationHistory history = DrinkWaterApplicationHistory.createApplicationHistory(this);
        applicationHistory = applicationHistory.append(history);

//        this.start();

        return history;
    }

    public void revertState() {
        this.stop();

        DrinkWaterApplicationHistory history = applicationHistory.last();

        java.util.List<ServiceConfiguration> previousConfig = DrinkWaterApplicationHistory.getConfig(history);

        ServiceConfigurationBuilder newBuilder = new ServiceConfigurationBuilder();
        newBuilder.addConfigurations(previousConfig);

        this.serviceBuilders = newBuilder;

        start();
    }

    public java.util.List<DrinkWaterApplicationHistory> history() {
        return applicationHistory.toJavaList();
    }


    public String getName() {
        return name;
    }

    public EventAggregator getEventAggregator() {
        return eventAggregator;
    }

    public void setEventLoggerClass(Class eventLoggerClass) {
        this.eventLoggerClass = eventLoggerClass;
    }

    public void addProperty(String property, String value) {
        initialApplicationProperties.setProperty(property, value);
    }

    //fixme : for now I assume only one store per app....
    public IDataStore getStore(String name) {
        return dataStores.get(0);
    }

    public enum ApplicationState {Up, Stopped}

    public java.util.List<IDrinkWaterService> getServices(){
        return services.toJavaList();
    }

}
