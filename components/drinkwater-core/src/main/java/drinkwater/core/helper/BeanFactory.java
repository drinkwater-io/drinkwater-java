package drinkwater.core.helper;

import drinkwater.IServiceConfiguration;
import drinkwater.InjectionStrategy;
import drinkwater.core.ServiceRepository;

import java.lang.reflect.Field;

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

        //inject fields eventually
        injectFields(beanToUse, config);

        injectDependencies(app, config, beanToUse);

        return beanToUse;

    }

    private static void injectDependencies(ServiceRepository app, Service config, Object beanToUse) throws IllegalAccessException {
        if (config.getConfiguration().getInjectionStrategy() == InjectionStrategy.Default) {
            for (IServiceConfiguration dependency : config.getConfiguration().getServiceDependencies()) {
                Object dependencyBean = app.getService(dependency.getServiceClass());

                //get a field corresponding to ype in the target bean
                for (Field f : beanToUse.getClass().getDeclaredFields()) {
                    if (f.getType().equals(dependency.getServiceClass())) {
                        f.setAccessible(true);
                        f.set(beanToUse, dependencyBean);
                    }
                }
            }
        }
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
