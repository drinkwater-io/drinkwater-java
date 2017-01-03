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
        if (config.configuration().getTargetBean() != null) {
            return createBeanObject(app, config);
        }
        return createBeanClass(app, config);

    }


    public static Object createBeanClass(ServiceRepository app, Service config) throws Exception {
        // create an instance of the bean
        Object beanToUse = config.configuration().getTargetBeanClass().newInstance();

        //inject fields eventually
        if (config.configuration().getInjectionStrategy() == InjectionStrategy.Default) {
            injectFields(beanToUse, config);
        }

        for (IServiceConfiguration dependency : config.configuration().getServiceDependencies()) {
            Object dependencyBean = app.getService(dependency.getServiceClass());

            //get a field corresponding to ype in the target bean
            for (Field f : beanToUse.getClass().getDeclaredFields()) {
                if (f.getType().equals(dependency.getServiceClass())) {
                    f.setAccessible(true);
                    f.set(beanToUse, dependencyBean);
                }
            }
        }




        return beanToUse;

    }

    public static Object createBeanObject(ServiceRepository app, Service config) throws Exception {
        // create an instance of the bean
        Object beanToUse = config.configuration().getTargetBean();

        //inject fields eventually
        if (config.configuration().getInjectionStrategy() == InjectionStrategy.Default) {
            injectFields(beanToUse, config);
        }

        for (IServiceConfiguration dependency : config.configuration().getServiceDependencies()) {
            Object dependencyBean = app.getService(dependency.getServiceClass());

            //get a field corresponding to ype in the target bean
            for (Field f : beanToUse.getClass().getDeclaredFields()) {
                if (f.getType().equals(dependency.getServiceClass())) {
                    f.setAccessible(true);
                    f.set(beanToUse, dependencyBean);
                }
            }
        }

        return beanToUse;

    }

    public static Object injectFields(Object bean, Service config) throws Exception {

        for (Field f : bean.getClass().getFields()) {
            String value = config.lookupProperty(config.configuration().getServiceClass().getSimpleName() + "." + f.getName());
            if (value != null) {
                f.set(bean, value);
            }
        }

        return bean;
    }
}
