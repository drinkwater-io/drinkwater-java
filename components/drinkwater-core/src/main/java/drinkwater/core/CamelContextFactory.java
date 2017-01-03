package drinkwater.core;

import drinkwater.IServiceConfiguration;
import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

/**
 * Created by A406775 on 3/01/2017.
 */
public class CamelContextFactory {

    public static DefaultCamelContext createCamelContext(IServiceConfiguration serviceConfiguration) {
        DefaultCamelContext ctx = createCamelContext(serviceConfiguration.getServiceName(), new SimpleRegistry());
        initProperties(ctx, serviceConfiguration);
        return ctx;
    }

    public static DefaultCamelContext createCamelContext(IServiceConfiguration serviceConfiguration, Registry registry) {
        DefaultCamelContext ctx = createCamelContext(serviceConfiguration.getServiceName(), registry);
        initProperties(ctx, serviceConfiguration);
        return ctx;
    }

    public static DefaultCamelContext createCamelContext(String name) {
        DefaultCamelContext ctx = createCamelContext(name, new SimpleRegistry());
        return ctx;
    }

    public static DefaultCamelContext createCamelContext(String name, Registry registry) {
        DefaultCamelContext ctx = createWithRegistry(registry);
        ctx.setName("CAMEL-CONTEXT-" + name);
        ctx.disableJMX();
        ctx.setStreamCaching(true);

//        Map<String, DataFormatDefinition> dataFormats = ctx.getDataFormats();
//        if (dataFormats == null) {
//            dataFormats = new HashMap<>();
//        }
//        dataFormats.put("drinkwater-jsonformat", new DataFormatDefinition(new DWJsonDataFormat()));
//
//
//        registerBean(ctx.getRegistry(), "drinkwater-jsonformat", new DWJsonDataFormat());

        return ctx;
    }

    public static void initProperties(CamelContext context, IServiceConfiguration configuration) {
        PropertiesComponent propertiesComponent = context.getComponent(
                "properties", PropertiesComponent.class);
        propertiesComponent.setIgnoreMissingLocation(true);
        propertiesComponent.setLocations(configuration
                .getPropertiesLocations());
    }

    public static String parseURI(CamelContext context, String uri) throws Exception {
        PropertiesComponent propertiesComponent = context.getComponent(
                "properties", PropertiesComponent.class);
        return propertiesComponent.parseUri(uri);
    }

    private static DefaultCamelContext createWithRegistry(Registry registry) {
        if (registry == null) {
            return new DefaultCamelContext();
        }

        return new DefaultCamelContext(registry);
    }

    public static void registerBean(CamelContext context, String beanName, Object bean) {
        registerBean(context.getRegistry(), beanName, bean);
    }

    public static void registerBean(Registry registry, String beanName, Object bean) {
        if (registry instanceof SimpleRegistry) {
            ((SimpleRegistry) registry).put(beanName, bean);
        } else if (registry instanceof PropertyPlaceholderDelegateRegistry) {
            Registry wrappedRegistry = ((PropertyPlaceholderDelegateRegistry) registry).getRegistry();
            registerBean(wrappedRegistry, beanName, bean);
        } else if (registry instanceof JndiRegistry) {
            ((JndiRegistry) registry).bind(beanName, bean);
        } else {
            throw new RuntimeException("could not identify the registry type while registering core beans");
        }

    }


}