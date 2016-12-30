package drinkwater.core.helper;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.rest.RestHelper;
import javaslang.collection.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

/**
 * Created by A406775 on 27/12/2016.
 */
public class RouteBuilders {

    public static RouteBuilder mapRestRoutes(DrinkWaterApplication app, InternalServiceConfiguration config) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //FIXME get all this from the config
                restConfiguration().component("jetty")
                        .host("localhost")
                        .port("8889")
                        .contextPath(config.getServiceClass().getSimpleName().toLowerCase())
                        .bindingMode(RestBindingMode.json);

                Object bean = BeanFactory.createBean(app, config);

                RestHelper.buildRestRoutes(this, bean);

            }
        };
    }

    //FIXME to many params
    public static RouteBuilder mapBeanClassRoutes(DrinkWaterApplication app, InternalServiceConfiguration config) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                List<Method> methods = javaslang.collection.List.of(config.getServiceClass().getDeclaredMethods());

                // create an instance of the bean
                Object beanToUse = BeanFactory.createBean( app,  config);

                for (Method m : methods) {
                    if(Modifier.isPublic(m.getModifiers())) {
                        from("direct:" + formatBeanMethodRoute(m))
                                .bean(beanToUse, formatBeanEndpointRoute(m), true);
                    }
                }
            }
        };
    }

    public static String formatBeanMethodRoute(Method m){
        String answer = m.getName();

        Parameter[] params = m.getParameters();

        java.util.List<String> paramList = new ArrayList<>();

        for (Parameter param : params) {
            paramList.add(param.getType().getName());
        }

        answer =  answer + "(" + String.join(",", paramList) + ")";

        return answer;

    }

    public static String formatBeanEndpointRoute(Method m){
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
