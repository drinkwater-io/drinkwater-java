package drinkwater.core;

import javaslang.collection.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Created by A406775 on 27/12/2016.
 */
public class BeanRouteBuilder {

    public static RouteBuilder mapBeanMethods(DrinkWaterApplication app, PropertiesComponent pc, ServiceConfiguration config) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                List<Method> methods = javaslang.collection.List.of(config.getServiceClass().getDeclaredMethods());

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

                for (Method m : methods) {
                    if(Modifier.isPublic(m.getModifiers())) {
                        from("direct:" + formatRoute(m))
                                .bean(beanToUse, formatBeanMethodCall(m), true);
                    }
                }
            }
        };
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

    public static String formatRoute(Method m){
        String answer = m.getName();

        Parameter[] params = m.getParameters();

        java.util.List<String> paramList = new ArrayList<>();

        for (Parameter param : params) {
            paramList.add(param.getType().getName());
        }

        answer =  answer + "(" + String.join(",", paramList) + ")";

        return answer;

    }

    public static String formatBeanMethodCall(Method m){
        String answer = m.getName();

        Parameter[] params = m.getParameters();
        java.util.List<String> paramList = new ArrayList<>();

        if(params.length > 0){

            paramList.add("${body}");
            for (int i = 1; i < params.length; i++) {
                paramList.add("${header.param" + i + "}");
            }
        }

        answer =  answer + "(" + String.join(",", paramList) + ")";

        return answer;
    }
}
