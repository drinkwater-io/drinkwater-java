package drinkwater.core.helper;

import drinkwater.*;
import drinkwater.core.DrinkWaterApplication;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by A406775 on 28/12/2016.
 */
public class BeanFactory {

    public static Object createBean(ServiceRepository app,
                                    IServiceConfiguration service,
                                    IPropertyResolver propertyResolver) throws Exception {
        if (service.getTargetBean() != null) {
            return createBeanObject(app, service, propertyResolver);
        }

        return createBeanClass(app, service, propertyResolver);

    }

    public static Object createBeanClass(ServiceRepository app,
                                         IServiceConfiguration service,
                                         IPropertyResolver propertyResolver) throws Exception {
        // create an instance of the bean
        Object beanToUse = service.getTargetBeanClass().newInstance();

        injectFields(beanToUse, service, propertyResolver);

        injectDependencies(app, service, beanToUse);

        injectStores(app, beanToUse);

        return beanToUse;

    }

    public static Object createBean(ServiceRepository app,
                                         IDataStoreConfiguration storeConfig) {
        try {
            // create an instance of the bean
            Object beanToUse = storeConfig.getImplementingClass().newInstance();

            injectFields(beanToUse, app, storeConfig);

            injectDependencies(app, beanToUse);

            injectStores(app, beanToUse);

            return beanToUse;
        }
        catch(Exception ex){
            throw new RuntimeException("could not create object : ", ex);
        }

    }

    public static Object createBeanObject(ServiceRepository app, IServiceConfiguration serviceConfiguration, IPropertyResolver propertyResolver) throws Exception {
        // create an instance of the bean
        Object beanToUse = serviceConfiguration.getTargetBean();

        if (serviceConfiguration.getInjectionStrategy() != InjectionStrategy.None) {
            if (!beanToUse.getClass().isAssignableFrom(serviceConfiguration.getTargetBeanClass())) {
                //we likely come from a serialization process so use the create from class
                beanToUse = serviceConfiguration.getTargetBeanClass().newInstance();
                //inject from hashmap
                //fixme we could better use the camel typeconversion facilities
                injectFields(beanToUse, (Map<String, Object>) serviceConfiguration.getTargetBean(), serviceConfiguration);
            } else {
                //inject fields eventually
                injectFields(beanToUse, serviceConfiguration, propertyResolver);
            }

            injectDependencies(app, serviceConfiguration, beanToUse);

            injectStores(app, beanToUse);
        }

        return beanToUse;

    }

    private static void injectStores(ServiceRepository app,
                              Object beanToUse) throws IllegalAccessException {


        //TODO : fix dependency management here we assume one store
        for (Field f : beanToUse.getClass().getDeclaredFields()) {

            if(IDataStore.class.isAssignableFrom(f.getType())){
                f.setAccessible(true);
                f.set(beanToUse, app.getStore(""));
            }

        }
    }

    private static void injectDependencies(ServiceRepository app,
                                           Object beanToUse) throws IllegalAccessException {

        //TODO : fix dependency management
        for (Field f : beanToUse.getClass().getFields()) {
            Annotation serviceAnnotation = f.getAnnotation(ServiceDependency.class);

            if (serviceAnnotation != null) {
                Object dependencyBean = app.getService(f.getType());
                f.setAccessible(true);
                f.set(beanToUse, dependencyBean);
            }
        }
    }

    private static void injectDependencies(ServiceRepository app,
                                           IServiceConfiguration config,
                                           Object beanToUse) throws IllegalAccessException {

        //TODO : fix dependency management
        for (Field f : beanToUse.getClass().getFields()) {
            Annotation serviceAnnotation = f.getAnnotation(ServiceDependency.class) ;

            if(serviceAnnotation != null){
                Object dependencyBean = app.getService(f.getType());
                f.setAccessible(true);
                f.set(beanToUse, dependencyBean);
            }
        }

        if (config.getInjectionStrategy() == InjectionStrategy.Default) {
            for (String dependency : config.getServiceDependencies()) {
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

    private static Object injectFields(Object bean, Map<String, Object> propertMap, IServiceConfiguration config) throws Exception {
        if (config.getInjectionStrategy() == InjectionStrategy.Default) {
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


    private static Object injectFields(Object bean,
                                       IServiceConfiguration config,
                                       IPropertyResolver propertyresolver) throws Exception {

        if (config.getInjectionStrategy() == InjectionStrategy.Default) {
            for (Field f : bean.getClass().getDeclaredFields()) {
                String propertyUri = f.getName() + ":undefined";
                String value = propertyresolver.lookupProperty(propertyUri);

                if (!"undefined".equals(value)) {
                    Object convertedValue = propertyresolver.lookupProperty(f.getType(),propertyUri);

                    f.setAccessible(true);
                    f.set(bean, convertedValue);
                }
            }
        }

        return bean;

    }

    private static Object injectFields(Object bean,
                                       IPropertyResolver propertyresolver,
                                       IDataStoreConfiguration storeConfig) throws Exception {

            for (Field f : bean.getClass().getFields()) {
                String propertyUri = f.getName() + ":undefined";

                String propertyValue = storeConfig.getProperty(propertyresolver, propertyUri);

                if (!"undefined".equals(propertyValue)) {
                    Object value = storeConfig.getProperty(propertyresolver, f.getType(), propertyUri);
                    if (value != null) {
                        f.setAccessible(true);
                        f.set(bean, value);
                    }
                }
            }

        return bean;

    }
}
