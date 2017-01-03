package drinkwater.core;

import drinkwater.core.helper.BeanFactory;
import drinkwater.core.helper.DefaultPropertyResolver;
import drinkwater.core.helper.Service;
import drinkwater.rest.RestHelper;
import javaslang.collection.List;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

/**
 * Created by A406775 on 27/12/2016.
 */
public class RouteBuilders {

    public static RoutesBuilder mapBeanRoutes(ServiceRepository serviceRepository,
                                              Service service) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                List<Method> methods = javaslang.collection.List.of(service.configuration().getServiceClass().getDeclaredMethods());

                // create an instance of the bean
                Object beanToUse = service.configuration().getTargetBean();

                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers())) {
                        from("direct:" + formatBeanMethodRoute(m))
                                .bean(beanToUse, formatBeanEndpointRoute(m), true);
                    }
                }
            }
        };
    }

    public static RouteBuilder mapRestRoutes(ServiceRepository app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                Object bean = BeanFactory.createBean(app, service);

                RestHelper.buildRestRoutes(this, bean, new DefaultPropertyResolver(service), service);

            }
        };
    }

    //FIXME to many params
    public static RouteBuilder mapBeanClassRoutes(ServiceRepository app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                List<Method> methods = javaslang.collection.List.of(service.configuration().getServiceClass().getDeclaredMethods());

                // create an instance of the bean
                Object beanToUse = BeanFactory.createBeanClass(app, service);

                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers())) {
                        from("direct:" + formatBeanMethodRoute(m))
                                .bean(beanToUse, formatBeanEndpointRoute(m), true);
                    }
                }
            }
        };
    }

    public static String formatBeanMethodRoute(Method m) {
        String answer = m.getName();

        Parameter[] params = m.getParameters();

        java.util.List<String> paramList = new ArrayList<>();

        for (Parameter param : params) {
            paramList.add(param.getType().getName());
        }

        answer = answer + "(" + String.join(",", paramList) + ")";

        return answer;

    }

    public static String formatBeanEndpointRoute(Method m) {
        String answer = m.getName();

        Parameter[] params = m.getParameters();
        java.util.List<String> paramList = new ArrayList<>();

        if (params.length > 0) {

            paramList.add("${body}");
            for (int i = 1; i < params.length; i++) {
                paramList.add("${header.param" + i + "}");
            }
        }

        answer = answer + "(" + String.join(",", paramList) + ")";

        return answer;
    }


}
