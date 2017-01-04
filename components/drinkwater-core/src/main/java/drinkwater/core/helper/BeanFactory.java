package drinkwater.core.helper;

import drinkwater.IServiceConfiguration;
import drinkwater.InjectionStrategy;
import drinkwater.core.ServiceRepository;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by A406775 on 28/12/2016.
 */
public class BeanFactory {

    public static Object createBean(ServiceRepository app, Service config) throws Exception {
        if (config.getConfiguration().getTargetBean() != null) {
            return createBeanObject(app, config);
        }

        return createBeanClass(app, config);

    }

    public static Object createBeanClass(ServiceRepository app, Service config) throws Exception {
        // create an instance of the bean
        Object beanToUse = config.getConfiguration().getTargetBeanClass().newInstance();

        injectFields(beanToUse, config);

        injectDependencies(app, config, beanToUse);

        return beanToUse;

    }

    public static Object createBeanObject(ServiceRepository app, Service config) throws Exception {
        // create an instance of the bean
        Object beanToUse = config.getConfiguration().getTargetBean();

        if (config.getConfiguration().getInjectionStrategy() != InjectionStrategy.None) {
            if (!beanToUse.getClass().isAssignableFrom(config.getConfiguration().getTargetBeanClass())) {
                //we likely come from a serialization process so use the create from class
                beanToUse = config.getConfiguration().getTargetBeanClass().newInstance();
                //inject from hashmap
                //fixme we could better use the camel typeconversion facilities
                injectFields(beanToUse, (Map<String, Object>) config.getConfiguration().getTargetBean(), config);
            } else {
                //inject fields eventually
                injectFields(beanToUse, config);
            }

            injectDependencies(app, config, beanToUse);
        }

        return beanToUse;

    }

    private static void injectDependencies(ServiceRepository app, Service config, Object beanToUse) throws IllegalAccessException {
        if (config.getConfiguration().getInjectionStrategy() == InjectionStrategy.Default) {
            for (String dependency : config.getConfiguration().getServiceDependencies()) {
                Object dependencyBean = app.getService(dependency);

                IServiceConfiguration dependencyDefinition = app.getServiceDefinition(dependency);

                //get a field corresponding to ype in the target bean
                for (Field f : beanToUse.getClass().getDeclaredFields()) {
                    if (f.getType().equals(dependencyDefinition.getServiceClass())) {
                        f.setAccessible(true);
                        f.set(beanToUse, dependencyBean);
                    }
                }
            }
        }
    }

    private static Object injectFields(Object bean, Map<String, Object> propertMap, Service config) throws Exception {
        if (config.getConfiguration().getInjectionStrategy() == InjectionStrategy.Default) {
            for (Field f : bean.getClass().getDeclaredFields()) {
                Object value = propertMap.getOrDefault(f.getName(), "undefined");
                if (!"undefined".equals(value)) {
                    f.setAccessible(true);
                    f.set(bean, value);
                }
            }
        }

        return bean;
    }


    private static Object injectFields(Object bean, Service config) throws Exception {

        if (config.getConfiguration().getInjectionStrategy() == InjectionStrategy.Default) {
            for (Field f : bean.getClass().getDeclaredFields()) {
                String value = config.lookupProperty(config.getConfiguration().getServiceName() + "." + f.getName() + ":undefined");
                if (!"undefined".equals(value)) {
                    f.setAccessible(true);
                    f.set(bean, value);
                }
            }
        }

        return bean;

    }
}
