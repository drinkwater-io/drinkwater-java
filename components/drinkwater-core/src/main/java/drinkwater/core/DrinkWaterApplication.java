package drinkwater.core;

import drinkwater.core.helper.ProducerTemplateInvocationHandler;
import drinkwater.core.helper.RouteBuilders;
import javaslang.collection.List;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;

//import javax.enterprise.inject.Vetoed;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
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

    List<ServiceConfigurationBuilder> serviceBuilders = List.empty();

    java.util.List<CamelContext> _camelContexts = new ArrayList<>();

    public void start() {

        for (ServiceConfigurationBuilder builder :serviceBuilders) {
            for (IServiceConfiguration config : builder.build()) {
                createCamelContextFromConfig(config);
            }
        }
    }

    public void stop() {
        for (CamelContext ctx : _camelContexts) {
            try {
                ctx.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addServiceBuilder(ServiceConfigurationBuilder builder){
        serviceBuilders = serviceBuilders.append(builder);
    }

    private void createCamelContextFromConfig(IServiceConfiguration config) {
        try {
            DefaultCamelContext ctx = new DefaultCamelContext();
            _camelContexts.add(ctx);
            PropertiesComponent prop = ctx.getComponent(
                    "properties", PropertiesComponent.class);
            prop.setLocation(config.getProperties());
            ctx.disableJMX();
            ctx.setName("CAMEL-CONTEXT-" + config.getServiceClass().getName());
            if(config.getScheme() == ServiceScheme.BeanClass) {
                ctx.addRoutes(RouteBuilders.mapBeanClassRoutes(this, prop, config));
            }
            else if(config.getScheme() == ServiceScheme.Rest){
                ctx.addRoutes(RouteBuilders.mapRestRoutes(this, prop, config));
            }
            ProducerTemplate template = ctx.createProducerTemplate();
            producertemplates.put(config.getServiceClass(), template);

            ctx.start();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T> T simpleProxy(Class<? extends T> iface, InvocationHandler handler, Class<?>...otherIfaces) {
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
