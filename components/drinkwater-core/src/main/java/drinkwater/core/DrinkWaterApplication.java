package drinkwater.core;

import drinkwater.*;
import drinkwater.core.helper.DefaultPropertyResolver;
import drinkwater.core.helper.Service;
import drinkwater.core.internal.*;
import drinkwater.core.reflect.BeanClassInvocationHandler;
import drinkwater.core.reflect.BeanInvocationHandler;
import drinkwater.helper.reflect.ReflectHelper;
import drinkwater.rest.RestInvocationHandler;
import drinkwater.rest.RestService;
import javaslang.collection.List;

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

    static {
        //FIXME manage the logging system
        java.util.logging.Logger topLogger = java.util.logging.Logger.getLogger("");
        topLogger.setLevel(Level.INFO);
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
    }

    public TracerBean tracer = new TracerBean();
    public JVMMetricsBean jvmMetricsBean = new JVMMetricsBean();
    private Logger logger = Logger.getLogger(DrinkWaterApplication.class.getName());
    private RestService restConfiguration = new RestService();
    private List<IDrinkWaterService> services = List.empty();
    private Map<Class, Object> serviceProxies = new HashMap<>();

    //public MetricRegistry metrics = new MetricRegistry();
    private CoreCamelContext masterCamelContext = new CoreCamelContext();
    private String name;

    private DrinkWaterApplication() {
        this(null);
    }

    private DrinkWaterApplication(String name) {
        if(name == null){
            name = "DrinkWaterApplication";
        }
        this.name = name;
    }

    public static DrinkWaterApplication create() {
        return new DrinkWaterApplication();
    }

    public static DrinkWaterApplication create(String name) {
        return new DrinkWaterApplication(name);
    }

    public TracerBean getTracer() {
        return tracer;
    }

    public void addServiceBuilder(ServiceConfigurationBuilder builder) {
        builder.build().forEach(this::addServiceConfig);
    }

    public void start() {

        restConfiguration.start();

        for (IDrinkWaterService config : services) {
            config.start();
        }

        ServiceManagementBean serviceManagement = new ServiceManagementBean(
                services.toJavaList(),
                tracer.getMetrics(),
                jvmMetricsBean.getMetrics());

        IServiceConfiguration config = ServiceConfiguration
                .forService(IServiceManagement.class)
                .useBean(serviceManagement)
                .name(this.name)
                .asRest();

        try {
            masterCamelContext.getCamelContext().addRoutes(
                    RouteBuilders.mapRestRoutes(this, new Service(masterCamelContext.getCamelContext(), config, tracer)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        masterCamelContext.start();

        logUsageInfo();
    }

    public void stop() {

        for (IDrinkWaterService config : services) {
            config.stop();
        }

        restConfiguration.stop();
    }

    private void addServiceConfig(IServiceConfiguration serviceConfig) {
        try {

            Service service = new Service(serviceConfig, tracer);

            service.configure(this);

            addProxy(service);

            services = services.append(service);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void addProxy(Service serviceToProxy) {
        if (serviceToProxy.configuration().getScheme() == ServiceScheme.BeanObject) {
            serviceProxies.put(serviceToProxy.configuration().getServiceClass(),
                    ReflectHelper.simpleProxy(serviceToProxy.configuration().getServiceClass(),
                            new BeanInvocationHandler(serviceToProxy.getCamelContext(), this, serviceToProxy)));
        } else if (serviceToProxy.configuration().getScheme() == ServiceScheme.BeanClass) {
            serviceProxies.put(serviceToProxy.configuration().getServiceClass(),
                    ReflectHelper.simpleProxy(serviceToProxy.configuration().getServiceClass(),
                            new BeanClassInvocationHandler(serviceToProxy.getCamelContext())));
        } else if (serviceToProxy.configuration().getScheme() == ServiceScheme.Rest) {
            serviceProxies.put(serviceToProxy.configuration().getServiceClass(),
                    ReflectHelper.simpleProxy(serviceToProxy.configuration().getServiceClass(),
                            new RestInvocationHandler(new DefaultPropertyResolver(serviceToProxy), serviceToProxy.configuration())));
        }
    }

    @Override
    public <T> T getService(Class<? extends T> iface) {
        return (T) serviceProxies.get(iface);
    }

    public void logUsageInfo() {
        logger.info("-----------------------DRINK WATER------------------------------------");
        logger.info("Management console is available here : http://localhost:9000");
        logger.info("-----------------------DRINK WATER------------------------------------");
    }

}
