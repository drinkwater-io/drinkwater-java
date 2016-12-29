package drinkwater.core;

import drinkwater.core.helper.InternalServiceConfiguration;
import drinkwater.core.helper.ProducerTemplateInvocationHandler;
import drinkwater.core.helper.RouteBuilders;
import javaslang.collection.List;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

//import javax.enterprise.inject.Vetoed;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

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

    Map<Class, ProducerTemplate> producertemplates = new HashMap<>();

    List<InternalServiceConfiguration> serviceConfigurations = List.empty();


    public void start() {

        for (InternalServiceConfiguration config : serviceConfigurations) {
            try {
                config.getCamelContext().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {

        for (InternalServiceConfiguration config : serviceConfigurations) {
            try {
                config.getCamelContext().stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            } else if (config.getScheme() == ServiceScheme.Rest) {
                ctx.addRoutes(RouteBuilders.mapRestRoutes(this, config));
            }

            producertemplates.put(config.getServiceClass(), ctx.createProducerTemplate());

            serviceConfigurations = serviceConfigurations.append(config);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T> T simpleProxy(Class<? extends T> iface, InvocationHandler handler, Class<?>... otherIfaces) {
        Class<?>[] allInterfaces = Stream.concat(
                Stream.of(iface),
                Stream.of(otherIfaces))
                .distinct()
                .toArray(Class<?>[]::new);

        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                allInterfaces,
                handler);
    }

    public <T> T getService(Class<? extends T> iface) {
        ProducerTemplate template = producertemplates.get(iface);
        return simpleProxy(iface, new ProducerTemplateInvocationHandler(template));
    }
}
