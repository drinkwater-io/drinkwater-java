package drinkwater.core;

import drinkwater.*;
import drinkwater.core.helper.DefaultPropertyResolver;
import drinkwater.core.helper.Service;
import drinkwater.core.internal.IServiceManagement;
import drinkwater.core.internal.JVMMetricsBean;
import drinkwater.core.internal.ServiceManagementBean;
import drinkwater.core.internal.TracerBean;
import drinkwater.core.reflect.BeanClassInvocationHandler;
import drinkwater.core.reflect.BeanInvocationHandler;
import drinkwater.helper.reflect.ReflectHelper;
import drinkwater.rest.RestInvocationHandler;
import drinkwater.rest.RestService;
import drinkwater.trace.BaseEvent;
import drinkwater.trace.EventAggregator;
import javaslang.collection.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.enterprise.inject.Vetoed;

/**
 * Created by A406775 on 27/12/2016.
 */
//@Vetoed
public class DrinkWaterApplication implements ServiceRepository {

    public static final String DW_STATICHANDLER = "dw-statichandler";
    private static Logger logger = Logger.getLogger(DrinkWaterApplication.class.getName());

    static {
        //FIXME manage the logging system
        java.util.logging.Logger topLogger = java.util.logging.Logger.getLogger("");
        topLogger.setLevel(Level.WARNING);
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
    }

    private List<DrinkWaterApplicationHistory> applicationHistory = List.empty();
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
    //fixme : should support multiple service builder
    private ServiceConfigurationBuilder serviceBuilders;

    private CamelContext applicationLevelContext;

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

    public static RouteBuilder createCoreRoutes(String managementHost) {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                from(String.format(
                        "jetty:http://%s?handlers=%s", managementHost, DW_STATICHANDLER))
                        .to("mock:empty?retainFirst=1");


            }
        };
    }

    public static RouteBuilder createTracingRoute(EventAggregator aggregator) {
        return new RouteBuilder() {

            ITraceLogger traceLgger = new ITraceLogger() {
                @Override
                public synchronized void logTrace(Object obj) {
                    if (!(obj instanceof Exchange)) {
                        logger.severe("tracing log must be an exchange");
                        return;
                    }
                    Exchange exchange = (Exchange) obj;

                    if (!(exchange.getIn().getBody() instanceof BaseEvent)) {
                        logger.severe("tracing log must be an exchange");
                        return;
                    }

                    BaseEvent event = (BaseEvent) exchange.getIn().getBody();

                    Method m = (Method) event.getPayload().getTarget()[0];

                    if (m.getName().equals("toString")) {
                        return;
                    }


                    System.out.println(event.getName() + " : " + event.getCorrelationId() + " -> " + event.getTime() + "  -  " + event.getDescription());

                    aggregator.addEvent(event);

                }
            };

            @Override
            public void configure() throws Exception {

                from("vm:trace").bean(traceLgger, "logTrace(${exchange})");

            }
        };
    }

    public static ResourceHandler getResourceHandler() {
        ResourceHandler staticHandler = new ResourceHandler();
        staticHandler.setBaseResource(Resource.newClassPathResource("/www"));
        return staticHandler;
    }

    public void startApplicationContext() {

        try {
            applicationLevelContext = CamelContextFactory.createCamelContext("applicationlevelContext");
            if (isTraceMaster) {
                applicationLevelContext.addRoutes(createTracingRoute(eventAggregator));
            }

            applicationLevelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopApplicationContext() {
        try {
            applicationLevelContext.stop();
        } catch (Exception e) {
            e.printStackTrace();
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
        serviceBuilders.configure();
        List.ofAll(serviceBuilders.getConfigurations()).forEach(this::addService);
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

        stopApplicationContext();

        logStoppedInfo();

        state = ApplicationState.Stopped;
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

    @Override
    public <T> T getService(Class<? extends T> iface) {
        return (T) serviceProxies.get(iface.getName());
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

    private void createAndStartTracing() {

    }

    private void createAndStartManagementService() {

        try {

            logger.info("starting management service");

            ServiceManagementBean serviceManagement = new ServiceManagementBean(
                    services.toJavaList(),
                    tracer.getMetrics(),
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

            CamelContextFactory.registerBean(managementService.getCamelContext(), DW_STATICHANDLER, getResourceHandler());

            String managementHost = managementService.lookupProperty(name + ".management.host:0.0.0.0");
            String managementPort = managementService.lookupProperty(name + ".management.port:9000");
            String managementHostAndPort = managementHost + ":" + managementPort;

            managementService.getCamelContext().addRoutes(createCoreRoutes(managementHostAndPort));

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

    private void logStoppedInfo() {
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

    public enum ApplicationState {Up, Stopped}
}
