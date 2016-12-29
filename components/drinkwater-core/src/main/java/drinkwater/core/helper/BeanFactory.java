package drinkwater.core.helper;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import org.apache.camel.component.properties.PropertiesComponent;

import java.lang.reflect.Field;

/**
 * Created by A406775 on 28/12/2016.
 */
public class BeanFactory {
    public static Object createBean(DrinkWaterApplication app, PropertiesComponent pc, ServiceConfiguration config) throws Exception {
        // create an instance of the bean
        Object beanToUse = config.getTargetBean().newInstance();

        //inject fields eventually
        if(config.getInjectionStrategy() == InjectionStrategy.Default){
            injectFields(beanToUse, pc, config);
        }

        for (ServiceConfiguration dependency: config.getServiceDependencies()) {
            Object dependencyBean = app.getService(dependency.getServiceClass());

            //get a field corresponding to ype in the target bean
            for (Field f: beanToUse.getClass().getDeclaredFields()) {
                if(f.getType().equals(dependency.getServiceClass()))
                {
                    f.setAccessible(true);
                    f.set(beanToUse, dependencyBean);
                }
            }
        }

        return beanToUse;

    }

    public static Object injectFields(Object bean, PropertiesComponent pc, ServiceConfiguration config) throws Exception{

        for (Field f: bean.getClass().getFields()) {
            String value = pc.parseUri(config.getServiceClass().getSimpleName() + "." + f.getName());
            if(value != null){
                f.set(bean, value);
            }
        }

        return bean;
    }
}
