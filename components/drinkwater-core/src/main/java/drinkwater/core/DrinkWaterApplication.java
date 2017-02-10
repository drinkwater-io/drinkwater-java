package drinkwater.core;

import drinkwater.*;
import drinkwater.core.helper.BeanFactory;
import drinkwater.core.helper.Service;
import drinkwater.core.helper.StoreProxy;
import drinkwater.core.internal.*;
import drinkwater.core.reflect.BeanClassInvocationHandler;
import drinkwater.core.reflect.BeanInvocationHandler;
import drinkwater.core.security.SimpleTokenProvider;
import drinkwater.helper.GeneralUtils;
import drinkwater.helper.reflect.ReflectHelper;
import drinkwater.rest.RestInvocationHandler;
import drinkwater.rest.RestService;
import drinkwater.security.ITokenProvider;
import drinkwater.trace.JavaLoggingEventLogger;
import javaslang.collection.List;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.util.StopWatch;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static drinkwater.DrinkWaterPropertyConstants.*;

//import javax.enterprise.inject.Vetoed;

/**
 * Created by A406775 on 27/12/2016.
 */
//@Vetoed
public class DrinkWaterApplication implements ServiceRepository, IPropertiesAware, IPropertyResolver, Closeable {

    public static final String DW_STATICHANDLER = "dw-static-management-handler";
    public static final String DW_STATIC_WWW_HANDLER = "dw-static-www-handler";

    private static Logger logger = LoggerFactory.getLogger(DrinkWaterApplication.class);

    private PropertiesComponent propertiesComponent;
    private List<DrinkWaterApplicationHistory> applicationHistory = List.empty();
    private List<StoreProxy> dataStores;
    private ApplicationState state = ApplicationState.Stopped;
    private String name;
    private TracerBean tracer;
    private JVMMetricsBean jvmMetricsBean;
    private RestService restConfiguration;
    private List<IDrinkWaterService> services;
    private Map<String, Object> serviceProxies;
    private Service managementService;
    private Service tokenService;
    private ApplicationOptions options;
    private Class eventLoggerClass = null;
    //fixme : should support multiple service builder
    private ApplicationBuilder applicationBuilder;
    private Properties initialApplicationProperties = new Properties();

    private CamelContext applicationLevelContext;

    private IBaseEventLogger currentBaseEventLogger;

    private DrinkWaterApplication() {
        this(null, null);
    }

    private DrinkWaterApplication(String name, ApplicationOptions applicationOptions) {
        if (name == null) {
            name = "drinkwater";
        }
        this.name = name;
        this.options = applicationOptions;

    }

    public static DrinkWaterApplication create() {
        return create((String) null);
    }

    public static DrinkWaterApplication create(String name) {
        return create(name, null);
    }

    public static DrinkWaterApplication create(ApplicationOptions options) {
        return create(null, options);
    }

    public static DrinkWaterApplication create(String name, ApplicationOptions options) {
        try {
            DrinkWaterApplication app = new DrinkWaterApplication(name, options);
            if (options != null) {
                if (options.getApplicationBuilderClass() != null) {
                    Constructor ct = options.getApplicationBuilderClass().getConstructor();
                    ct.setAccessible(true);
                    ApplicationBuilder builder = (ApplicationBuilder) ct.newInstance();
                    app.addServiceBuilder(builder);
                }
                if (options.isAutoStart()) {
                    app.start();
                }
            }
            return app;
        } catch (Exception ex) {
            throw new RuntimeException(
                    "could not create application you should at" +
                            "least declare a default public constructor", ex);
        }
    }


    private void startTokenServiceIfEnabled() {
        if (this.safeLookupProperty(Boolean.class, Authentication_token_service_enabled, false)) {

            ServiceConfiguration config =
                    (ServiceConfiguration) new ServiceConfiguration()
                            .forService(ITokenProvider.class)
                            .useBeanClass(SimpleTokenProvider.class)
                            .addInitialProperty(Authentication_token_service_enabled, false)
                            .name("auth")
                            .asRest();

            tokenService = createServiceFromConfig(config, tracer);

            tokenService.start();

        }
    }

    private void stopTokenServiceIfEnabled() {
        if (tokenService != null) {
            tokenService.stop();
        }
    }


    private static void addWWWIfEnabled(DrinkWaterApplication app) throws Exception {
        Boolean serveStaticWWW = Boolean.parseBoolean(app.lookupProperty(WWW_Enabled));

        if (serveStaticWWW) {
            RouteBuilder WWWRouteBuilder = new RouteBuilder() {

                @Override
                public void configure() throws Exception {


                    CamelContextFactory.registerBean(app.applicationLevelContext, DW_STATIC_WWW_HANDLER, getWWWResourceHandler());

                    String wwwHost = app.lookupProperty(WWW_Host);
                    String wwwPort = app.lookupProperty(WWW_Port);
                    String wwwRoot = app.lookupProperty(WWW_Root);
                    String wwwHostAndPort = wwwHost + ":" + wwwPort + "/" + wwwRoot;

                    from(String.format(
                            "jetty:http://%s?handlers=%s", wwwHostAndPort, DW_STATIC_WWW_HANDLER))
                            .to("mock:empty?retainFirst=1");

                    logger.info("static files served from : http://" + wwwHostAndPort);


                }
            };
            app.applicationLevelContext.addRoutes(WWWRouteBuilder);
        }
    }

    private boolean isUseServiceManagement() {
        boolean fromAppBuilder = applicationBuilder.isUseServiceManagement();
        return safeLookupProperty(Boolean.class, "useServiceManagement", fromAppBuilder);
    }

    private boolean isUseTracing() {
        boolean fromAppBuilder = applicationBuilder.isUseTracing();
        return safeLookupProperty(Boolean.class, "useTracing", fromAppBuilder);
    }

    private static void addManagementWebApplication(DrinkWaterApplication app) throws Exception {
        if (app.isUseServiceManagement()) {

            RouteBuilder ManagementWebApplicationRouteBuilder = new RouteBuilder() {

                @Override
                public void configure() throws Exception {

                    String managementHostAndPort = "notInitialized";

                    try {
                        CamelContextFactory.registerBean(app.applicationLevelContext,
                                DW_STATICHANDLER,
                                getManagementResourceHandler());

                        String managementHost = app.lookupProperty(Management_Host);
                        String managementPort = app.lookupProperty(Management_Port);
                        managementHostAndPort = managementHost + ":" + managementPort;

                        from(String.format(
                                "jetty:http://%s?handlers=%s", managementHostAndPort, DW_STATICHANDLER))
                                .to("mock:empty?retainFirst=1");

                        logger.info("management web page can be found here : http://" + managementHostAndPort);

                        from(String.format(
                                "jetty:http://%s/stopApplication?httpMethodRestrict=POST", managementHostAndPort))
                                .bean(new ShutDownDrinkWaterBean(app)).id("app_shutdown");
                    } catch (Exception ex) {
                        String message = "could not start management web application on : " + managementHostAndPort;
                        logger.error(message);
                        throw new Exception(message, ex);
                    }
                }


            };
            app.applicationLevelContext.addRoutes(ManagementWebApplicationRouteBuilder);

        }
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

    private RouteBuilder createTracingRoute(IBaseEventLogger logger) {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                from("vm:trace").bean(logger, "logEvent(${body})");

            }
        };
    }

    private ApplicationOptions getOptions() {
        return new ApplicationOptions();
    }


    public synchronized void startTracingRoute() {
        try {
            if (isUseTracing()) {

                //get logger class
                Class loggerImplementationClass = eventLoggerClass == null ? JavaLoggingEventLogger.class : eventLoggerClass;
                ServiceConfiguration traceBeanConfig = new ServiceConfiguration();
                traceBeanConfig.setServiceName("eventLogger");
                traceBeanConfig.setServiceClass(IBaseEventLogger.class);
                traceBeanConfig.setTargetBeanClass(loggerImplementationClass);
                traceBeanConfig.setScheme(ServiceScheme.BeanClass);
                traceBeanConfig.useTracing(false);
                //initialApplicationProperties.forEach((key, value) -> traceBeanConfig.addInitialProperty(key.toString(), value.toString()));

                Service tracingService = createServiceFromConfig(traceBeanConfig, tracer);

                currentBaseEventLogger = (IBaseEventLogger) BeanFactory.createBean(this, tracingService.getConfiguration(), tracingService);

                applicationLevelContext.addRoutes(createTracingRoute(currentBaseEventLogger));
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
            applicationLevelContext = CamelContextFactory.createCamelContext(this);
            applicationLevelContext.getShutdownStrategy().setTimeout(3);
            applicationLevelContext.getShutdownStrategy().setTimeUnit(TimeUnit.SECONDS);
            applicationLevelContext.start();

            addWWWIfEnabled(this);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void stopApplicationContext() {
        try {
            logger.info(String.format("Shutting down application context for  %s .... goodbye !!!", this.getPropertiesDefaultName()));
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
        dataStores = List.empty();
        tracer = new TracerBean();
        jvmMetricsBean = new JVMMetricsBean();
        restConfiguration = new RestService();
//        if (isTraceMaster) {
//            eventAggregator.clear();
//        }
//        eventAggregator = new EventAggregator();
    }

    public ApplicationBuilder configuration() {
        return applicationBuilder;
    }

    public TracerBean getTracer() {
        return tracer;
    }

    public void addServiceBuilder(ApplicationBuilder builder) {

        this.applicationBuilder = builder;

//        builder.configure();
//
//        List.ofAll(builder.getConfigurations()).forEach(this::addService);
    }


    private void cofigureServices() {

        if (applicationBuilder != null) {
//            applicationBuilder.configure();
            List.ofAll(applicationBuilder.getStores()).forEach(this::addStore);
            List.ofAll(applicationBuilder.getConfigurations()).forEach(this::addService);

        } else {
            //TODO add explanation how to ad a srvice
            logger.warn("no service builder initialized, add at leas one service");
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
            throw new RuntimeException("Cannot start " + getApplicationName() + " twice");
        }

        StopWatch timingWath = new StopWatch();

        try {

            logStartInfo();
            if (applicationBuilder != null) {
                applicationBuilder.configure();
            }

            this.eventLoggerClass = applicationBuilder.getEventLoggerClass();

            startApplicationContext();

            cleanBeforeStart();

            startExternalServices();

            cofigureServices();

            startDataStores();

            startTracingRoute();

            startTokenServiceIfEnabled();


            for (IDrinkWaterService service : services) {
                service.start();
            }

            if (isUseServiceManagement()) {
                createAndStartManagementService();
            }

            state = ApplicationState.Up;
        } finally {
            timingWath.stop();
        }

        logStartedInfo(timingWath);
    }

    public void stop() {
        if (isStopped()) {
            logger.info(String.format("application %s was already stopped ", name));
            return;
        }

        StopWatch timingWath = new StopWatch();
        try {

            logStopingInfo();

            for (IDrinkWaterService service : services) {
                service.stop();
            }

            stopExternalServices();

            if (isUseServiceManagement()) {
                stopManagementService();
            }

            stopTokenServiceIfEnabled();

            stopDataStores();

            stopApplicationContext();

            state = ApplicationState.Stopped;
            timingWath.stop();
        } finally {
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
                throw new RuntimeException("could not start or migrate datastore2", e);
            }

        }


    }

    private void stopDataStores() {

        for (IDataStore store :
                dataStores) {

            try {
                store.close();
            } catch (Exception e) {
                throw new RuntimeException("could not close datastore2", e);
            }

        }

    }

    private void addService(IServiceConfiguration serviceConfig) {
        Service s = createServiceFromConfig(serviceConfig, tracer);
        services = services.append(s);
        addProxy(s);
    }

    private void addStore(IDataStoreConfiguration serviceConfig) {
        StoreProxy s = createStoreFromConfig(serviceConfig);
        dataStores = dataStores.append(s);
        //addProxy(s);
    }

    private StoreProxy createStoreFromConfig(IDataStoreConfiguration serviceConfig) {
        try {

            StoreProxy store = new StoreProxy(this, serviceConfig);

            store.configure();

            return store;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
                            new RestInvocationHandler(serviceToProxy, serviceToProxy)));
        }
    }

    //TODO : it could be that more than one implements a service class
    @Override
    public <T> T getService(Class<? extends T> iface) {
        T service = null;
        //check in service if there is one that serves iface
        if (services.filter(s -> s.getConfiguration().getServiceClass().equals(iface)).size() > 0) {
            service = (T) serviceProxies.get(services.filter(s -> s.getConfiguration().getServiceClass().equals(iface)).get().getConfiguration().getServiceName());
        }
        if (service == null) {
            service = (T) serviceProxies.get(iface.getName());
        }
        return service;
    }

    @Override
    public <T> T getService(String serviceName) {
        return (T) serviceProxies.get(serviceName);
    }


    //fixme : for now I assume only one store per app....
    //we could filter on name
    public <T> T getStore(String name) {
        return (T) dataStores.get(0).getTarget();
    }

    public IServiceConfiguration getServiceDefinition(String serviceName) {
        return
                services.filter(s -> s.getConfiguration().getServiceName().equals(serviceName))
                        .map(s -> s.getConfiguration())
                        .get();
    }

    public IDrinkWaterService getDrinkWaterService(String serviceName) {
        return
                services.filter(s -> s.getConfiguration().getServiceName().equals(serviceName))
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

            addManagementWebApplication(this);

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
        logger.info(DrinkWaterLogo.asAscii());
    }

    private void logStartedInfo(StopWatch stoppedwatch) {

        Iterator<String> services = getServices().stream()
                .sorted(Comparator.comparing(s -> s.getConfiguration().getScheme()))
                .map(Object::toString)
                .iterator();
        logger.info("configured services :");
        services.forEachRemaining(s -> logger.info("  > " + s));
        logger.info(String.format("-----------------------application %s STARTED in %d ms, have a nice day------------------------------------", name, stoppedwatch.taken()));
    }

    private void logStopingInfo() {
        logger.info("-----------------------STOPPING " + name + "------------------------------------");
    }

    private void logStoppedInfo(StopWatch stoppedwatch) {
        logger.info(String.format("-----------------------application %s STOPPED in %d ms, have a nice day------------------------------------", name, stoppedwatch.taken()));
    }

    //fixme : it should be possible to restart only some services...
    public void patchService(String serviceName, Object beanObject) {

        this.stop();

        ApplicationBuilder newBuilder = new ApplicationBuilder(applicationBuilder.getConfigurations());
        IServiceConfiguration config = newBuilder.getConfiguration(serviceName);
        config.setScheme(ServiceScheme.BeanObject);
        config.setInjectionStrategy(InjectionStrategy.None);
        config.setTargetBean(beanObject);

        this.applicationBuilder = newBuilder;

        this.start();
    }

    public void patchService(String serviceName, IServiceConfiguration patchConfig) {

        this.stop();

        ApplicationBuilder newBuilder = new ApplicationBuilder(applicationBuilder.getConfigurations());
        IServiceConfiguration config = newBuilder.getConfiguration(serviceName);
        config.patchWith(patchConfig);

        this.applicationBuilder = newBuilder;

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

        ApplicationBuilder newBuilder = new ApplicationBuilder();
        newBuilder.addConfigurations(previousConfig);

        this.applicationBuilder = newBuilder;

        start();
    }

    public java.util.List<DrinkWaterApplicationHistory> history() {
        return applicationHistory.toJavaList();
    }


    public String getPropertiesDefaultName() {
        return name;
    }

//    public EventAggregator getEventAggregator() {
//        return eventAggregator;
//    }

//    public void setEventLoggerClass(Class eventLoggerClass) {
//        this.eventLoggerClass = eventLoggerClass;
//    }

    public void addProperty(String property, String value) {
        initialApplicationProperties.setProperty(property, value);
    }


    @Override
    public Properties getInitialProperties() {
        return initialApplicationProperties;
    }

    @Override
    public String[] getPropertiesLocations() {

        String generalClassPathLocation = "classpath:drinkwater-application.properties";
        String classPathLocation = "classpath:" + getApplicationName() + ".properties";
        String fileLocation = "file:" +
                Paths.get(GeneralUtils.getJarFolderPath(this.getClass()).toString(),
                        getApplicationName() + ".properties");

        String[] defaultLocations = new String[]{generalClassPathLocation, classPathLocation, fileLocation};

        return defaultLocations;
    }

    @Override
    public String getpropertiesPrefix() {
        return getPropertiesDefaultName();
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    public enum ApplicationState {Up, Stopped}

    public java.util.List<IDrinkWaterService> getServices() {
        return services.toJavaList();
    }

    public String getApplicationName() {
        if (name == null) {
            return "drinkwater-application";
        }
        return name;
    }

    @Override
    public String lookupProperty(String s) throws Exception {
        //create the propertyprefix
        String prefix = this.getApplicationName() + ".";
        return getPropertiesComponent().parseUri(prefix + s);
    }

    @Override
    public Object lookupProperty(Class resultType, String uri) throws Exception {
        String value = lookupProperty(uri);
        return this.applicationLevelContext.getTypeConverter().convertTo(resultType, value);
    }

    @Override
    public <T> T safeLookupProperty(Class<T> resultType, String uri, T defaultIfUnsafe) {
        try {
            T result = (T) lookupProperty(resultType, uri);
            return result;
        } catch (Exception e) {
            return defaultIfUnsafe;
        }
    }

    public PropertiesComponent getPropertiesComponent() {
        if (propertiesComponent == null) {
            propertiesComponent = applicationLevelContext.getComponent(
                    "properties", PropertiesComponent.class);
        }
        return propertiesComponent;
    }




}
