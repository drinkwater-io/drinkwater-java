package drinkwater.helper;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

public class CamelContextHelper {
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
