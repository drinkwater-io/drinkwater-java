package drinkwater.core;

import drinkwater.IPropertiesAware;
import drinkwater.IServiceConfiguration;
import drinkwater.core.helper.PropertiesResolver;
import drinkwater.helper.MapUtils;
import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

import java.util.Properties;

/**
 * Created by A406775 on 3/01/2017.
 */
public class CamelContextFactory {

    public static DefaultCamelContext createCamelContext(DrinkWaterApplication dwa, IServiceConfiguration serviceConfiguration) {
        return createCamelContext(dwa, serviceConfiguration, null);
    }

    public static DefaultCamelContext createCamelContext(DrinkWaterApplication dwa, IServiceConfiguration serviceConfiguration, Registry registry) {
        if (registry == null) {
            registry = new SimpleRegistry();
        }
        DefaultCamelContext ctx = createCamelContext("Service-Context-", serviceConfiguration.getServiceName(), registry);
        initProperties(ctx, dwa, serviceConfiguration);
        return ctx;
    }

    public static DefaultCamelContext createCamelContext(DrinkWaterApplication application) {
        DefaultCamelContext ctx = createCamelContext("Application-Context-", application.getPropertiesDefaultName(), new SimpleRegistry());
        initProperties(ctx, application, null);
        return ctx;
    }

    private static DefaultCamelContext createCamelContext(String prefix, String name, Registry registry) {
        DefaultCamelContext ctx = createWithRegistry(registry);
        ctx.setName(prefix + name);
        ctx.disableJMX();
        ctx.setStreamCaching(true);
        return ctx;
    }

    private static void initProperties(CamelContext context,
                                      IPropertiesAware applicationPropertiesAware,
                                      IPropertiesAware servicePropertiesAware) {

        try {
            PropertiesComponent propertiesComponent = context.getComponent(
                    "properties", PropertiesComponent.class);
            propertiesComponent.setIgnoreMissingLocation(true);

            String applicationPrefix = applicationPropertiesAware.getpropertiesPrefix();
            String servicePrefix = null;
            if(servicePropertiesAware != null) {
                servicePrefix = applicationPrefix + "." + servicePropertiesAware.getpropertiesPrefix();
            }

            Properties applicationProperties =
                    loadProperties(applicationPrefix, applicationPropertiesAware);
            Properties serviceProperties =
                    loadProperties(servicePrefix, servicePropertiesAware);

            Properties initialServiceProperties = new Properties();
            if(servicePropertiesAware != null){
                initialServiceProperties = MapUtils.prefixProperties(servicePrefix, servicePropertiesAware.getInitialProperties());
            }

            Properties initialApplicationProperties = new Properties();
            if(servicePropertiesAware != null){
                initialApplicationProperties = MapUtils.prefixProperties(applicationPrefix, applicationPropertiesAware.getInitialProperties());
            }

            Properties mergedProps = MapUtils.mergeProperties(
                    applicationProperties,
                    serviceProperties);
            mergedProps = MapUtils.mergeProperties(
                    initialApplicationProperties,
                    mergedProps);
            mergedProps =MapUtils.mergeProperties(
                    initialServiceProperties,
                    mergedProps);
            propertiesComponent.setInitialProperties(mergedProps);
        } catch (Exception ex) {
            throw new RuntimeException("could not initialize application properties", ex);
        }
    }

    private static Properties loadProperties(String prefix, IPropertiesAware applicationProperties) throws Exception {
        if(applicationProperties == null){
            return null;
        }
        PropertiesResolver pr = new PropertiesResolver(applicationProperties);
        return pr.resolveProperties(prefix);

    }

//    public static String parseURI(CamelContext context, String uri) throws Exception {
//        PropertiesComponent propertiesComponent = context.getComponent(
//                "properties", PropertiesComponent.class);
//        return propertiesComponent.parseUri(uri);
//    }

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
