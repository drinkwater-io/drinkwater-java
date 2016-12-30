package drinkwater.core;

import drinkwater.*;
import drinkwater.helper.reflect.ReflectHelper;
import drinkwater.core.helper.InternalServiceConfiguration;
import drinkwater.core.reflect.BeanClassInvocationHandler;
import drinkwater.core.helper.RouteBuilders;
import drinkwater.rest.RestInvocationHandler;
import drinkwater.rest.RestServiceConfiguration;
import javaslang.collection.List;
import org.apache.camel.impl.DefaultCamelContext;

//import javax.enterprise.inject.Vetoed;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by A406775 on 27/12/2016.
 */
//@Vetoed
public class DrinkWaterApplication {

    static {
        //FIXME manage the logging system
        java.util.logging.Logger topLogger = java.util.logging.Logger.getLogger("");
        topLogger.setLevel(Level.INFO);
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
    }

    RestServiceConfiguration restConfiguration = new RestServiceConfiguration();

    List<InternalServiceConfiguration> serviceConfigurations = List.empty();

    Map<Class, Object> serviceProxies = new HashMap<>();

    public void start() {

        //start services
        restConfiguration.start();

        for (InternalServiceConfiguration config : serviceConfigurations) {
            try {
                config.getCamelContext().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {

        //stop services
        for (InternalServiceConfiguration config : serviceConfigurations) {
            try {
                config.getCamelContext().stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        restConfiguration.stop();
    }

    public void addServiceBuilder(ServiceConfigurationBuilder builder) {
        builder.build().forEach(this::addServiceConfig);
    }

    private void addServiceConfig(IServiceConfiguration serviceConfig) {
        try {

            DefaultCamelContext ctx = new DefaultCamelContext();
            ctx.disableJMX();
            ctx.setName("CAMEL-CONTEXT-" + serviceConfig.getServiceClass().getName());

            InternalServiceConfiguration config =
                    new InternalServiceConfiguration(serviceConfig, ctx);
            if (config.getScheme() == ServiceScheme.BeanClass) {
                ctx.addRoutes(RouteBuilders.mapBeanClassRoutes(this, config));
                serviceProxies.put(config.getServiceClass(),
                        ReflectHelper.simpleProxy(config.getServiceClass(),
                                new BeanClassInvocationHandler(ctx)));
            } else if (config.getScheme() == ServiceScheme.Rest) {
                ctx.addRoutes(RouteBuilders.mapRestRoutes(this, config));
                serviceProxies.put(config.getServiceClass(),
                        ReflectHelper.simpleProxy(config.getServiceClass(),
                                new RestInvocationHandler(ctx)));
            }

            serviceConfigurations = serviceConfigurations.append(config);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> T getService(Class<? extends T> iface) {
        return (T) serviceProxies.get(iface);
    }

}
