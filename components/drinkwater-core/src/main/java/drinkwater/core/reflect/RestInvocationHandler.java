package drinkwater.core.reflect;

import drinkwater.rest.Rest;
import org.apache.camel.ProducerTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by A406775 on 29/12/2016.
 */

public class RestInvocationHandler implements InvocationHandler {

    private final ProducerTemplate producerTemplate;

    public RestInvocationHandler(ProducerTemplate template) {
        this.producerTemplate = template;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        return Rest.invoke(proxy, method,  args);

//
//        String restPath = RestRouteBuilder.restPathFor(method);
////
////        Class returnType = method.getReturnType();
////
////        boolean isPrimitiveOrWrapped =
////                ClassUtils.isPrimitiveOrWrapper(returnType);
////
////        if(isPrimitiveOrWrapped) {
////            Unirest.get("http://localhost:8889/idrinktrackerservice/" + restPath).asJ;
////        }
//
//        String endoint = "restlet:http://localhost:8889/idrinktrackerservice/" + restPath + "?restletMethod=POST";
//
//        producerTemplate.setDefaultEndpointUri(endoint);
//
////        Object obj = producerTemplate.requestBodyAndHeader(endoint, null, "CamelHttpQuery" , "volume=10");
//        Object obj = producerTemplate.requestBodyAndHeader(endoint, args[0], "volume" , args[1]);


//        if (args == null || args.length == 0) {
//            result = producerTemplate.requestBody(route, (Object) null);
//        } else if (args.length == 1) {
//            result = producerTemplate.requestBody(route, args[0]);
//        } else {
//            result = producerTemplate.requestBodyAndHeaders(route, args[0], getMap(args));
//        }

        //return obj;
    }

    public static Map<String, Object> getMap(Object[] args) {

        Map<String, Object> answer = new HashMap<>();

        for (int i = 1; i < args.length; i++) {
            answer.put("param" + i, args[i]);
        }
        return answer;
    }
}
