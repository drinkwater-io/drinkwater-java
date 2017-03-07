package drinkwater.bean;

import drinkwater.Builder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class BeanServiceBuilder extends Builder<BeanServiceBuilder> {


    @Override
    public RouteDefinition exposeService(RouteBuilder rb, Method method) {

        return rb.from("direct:" + formatBeanMethodRoute(method));

    }

    @Override
    public void targetService(RouteDefinition processDefinition, Method method) {
        processDefinition.bean(getBean(), formatBeanEndpointRoute(method), true);
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
