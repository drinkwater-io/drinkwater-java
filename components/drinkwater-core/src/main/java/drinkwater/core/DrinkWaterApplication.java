package drinkwater.core;

import drinkwater.IServiceConfiguration;
import drinkwater.ServiceConfigurationBuilder;
import drinkwater.ServiceScheme;
import drinkwater.core.helper.DefaultPropertyResolver;
import drinkwater.core.helper.InternalServiceConfiguration;
import drinkwater.core.reflect.BeanClassInvocationHandler;
import drinkwater.core.reflect.BeanInvocationHandler;
import drinkwater.helper.reflect.ReflectHelper;
import drinkwater.rest.RestInvocationHandler;
import drinkwater.rest.RestService;
import javaslang.collection.List;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

//import javax.enterprise.inject.Vetoed;

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

    RestService restConfiguration = new RestService();

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
            if (config.getScheme() == ServiceScheme.BeanObject) {
                // ctx.addRoutes(RouteBuilders.mapBeanRoutes(this, config));
                serviceProxies.put(config.getServiceClass(),
                        ReflectHelper.simpleProxy(config.getServiceClass(),
                                new BeanInvocationHandler(ctx, this, config)));
            } else if (config.getScheme() == ServiceScheme.BeanClass) {
                ctx.addRoutes(RouteBuilders.mapBeanClassRoutes(this, config));
                serviceProxies.put(config.getServiceClass(),
                        ReflectHelper.simpleProxy(config.getServiceClass(),
                                new BeanClassInvocationHandler(ctx)));
            } else if (config.getScheme() == ServiceScheme.Rest) {
                ctx.addRoutes(RouteBuilders.mapRestRoutes(this, config));
                serviceProxies.put(config.getServiceClass(),
                        ReflectHelper.simpleProxy(config.getServiceClass(),
                                new RestInvocationHandler(new DefaultPropertyResolver(config), config)));
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
