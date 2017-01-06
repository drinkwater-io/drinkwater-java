package drinkwater.core.reflect;

import drinkwater.core.RouteBuilders;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by A406775 on 27/12/2016.
 */
public class BeanClassInvocationHandler implements InvocationHandler {

    private final ProducerTemplate producerTemplate;


    public BeanClassInvocationHandler(CamelContext context) {
        this.producerTemplate = context.createProducerTemplate();
    }

    public static Map<String, Object> getMap(Object[] args) {

        Map<String, Object> answer = new HashMap<>();

        for (int i = 1; i < args.length; i++) {
            answer.put("param" + i, args[i]);
        }
        return answer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;

        String route = "direct:" + RouteBuilders.formatBeanMethodRoute(method);

        if (args == null || args.length == 0) {
            result = producerTemplate.requestBody(route, (Object) null);
        } else if (args.length == 1) {
            result = producerTemplate.requestBody(route, args[0]);
        } else {
            result = producerTemplate.requestBodyAndHeaders(route, args[0], getMap(args));
        }

        return result;
    }
}
