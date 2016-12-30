package drinkwater.core.rest;

import drinkwater.core.reflect.ReflectHelper;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by A406775 on 20/12/2016.
 */

public class RestRouteBuilderHelper {

    private static Map<HttpMethods, String[]> prefixesMap = new HashMap<>();

    //FIXME : get it from some config?
    static{
        prefixesMap.put(HttpMethods.GET, new String[]{"get","find", "check"});
        prefixesMap.put(HttpMethods.POST, new String[]{"save","create", "set"});
        prefixesMap.put(HttpMethods.DELETE, new String[]{"delete", "remove", "clear"});
    }

    public static HttpMethods getCorrespondingHttpMethod(String methodName){
        return List.ofAll(prefixesMap.entrySet())
                .filter(prefix -> startsWithOneOf(methodName, prefix.getValue()))
                .map(entryset -> entryset.getKey())
                .getOrElse(HttpMethods.OPTIONS);
    }

    private static boolean startsWithOneOf(String value, String[] prefixes){
        return List.of(prefixes)
                .filter(p -> value.toLowerCase().startsWith(p))
                .length() > 0;
    }

    public static String restPathFor(Method method){
        HttpMethods httpMethod = getCorrespondingHttpMethod(method.getName());

        String restPath = restPath(method, httpMethod);

        return  restPath;
    }


    public static Tuple2<RestDefinition,String> buildRestRoute(RouteBuilder builder, Method method){

        HttpMethods httpMethod = getCorrespondingHttpMethod(method.getName());

        String restPath = restPathFor(method);

        RestDefinition restDefinition =
                toRestdefinition(builder, method, httpMethod, restPath);

        String camelMethod = camelMethodBuilder(method, httpMethod);

        return Tuple.of(restDefinition, camelMethod);

    }

    public static void buildRestRoutes(RouteBuilder builder, Object bean){
        javaslang.collection.List.of(ReflectHelper.getPublicDeclaredMethods(bean.getClass()))
                .map(method -> buildRestRoute(builder, method))
                .map(tuple -> routeToBeanMethod(tuple._1, bean, tuple._2));
    }

    private static String restPath(Method method, HttpMethods httpMethod){
        if(httpMethod == HttpMethods.OPTIONS){
            return "";
        }
        String fromPath = getPath(method);

        if (fromPath == null) {
            fromPath = List.of(prefixesMap.get(httpMethod))
                    .filter(prefix -> method.getName().toLowerCase().startsWith(prefix))
                    .map(prefix -> method.getName().replace(prefix, "").toLowerCase())
                    .getOrElse("");
        }

        if (httpMethod == HttpMethods.GET) {
            if (fromPath == null || fromPath.isEmpty()) {
                fromPath = javaslang.collection.List.of(method.getParameters())
                        .map(p -> "{" + p.getName() + "}").getOrElse("");
            }
        }

        return fromPath;
    }


    private static RestDefinition toRestdefinition(RouteBuilder builder,
                                                   Method method,
                                                   HttpMethods httpMethod,
                                                   String restPath) {
        RestDefinition answer = builder.rest();

        String fromPath = restPath;

        if (httpMethod == HttpMethods.GET) {
            answer = answer.get(fromPath);
        } else if (httpMethod == HttpMethods.POST) {
            answer = answer.post(fromPath).type(method.getParameters()[0].getType());
        } else if (httpMethod == HttpMethods.PUT) {
            answer = answer.put(fromPath);
        } else if (httpMethod == HttpMethods.DELETE) {
            answer = answer.delete(fromPath);
        } else if (httpMethod == HttpMethods.PATCH) {
            answer = answer.patch(fromPath);
        }
        else {
            throw new RuntimeException("method currently not supported in Rest Paths : " + httpMethod);
        }

        return answer;
    }

    private static String camelMethodBuilder(Method m, HttpMethods httpMethod) {
        String params = "";
        if (httpMethod == HttpMethods.GET) {
            params = javaslang.collection.List.of(m.getParameters())
                    .map(p -> "${header." + p.getName() + "}")
                    .mkString(",");
            params = "(" + params + ")";
        }

        if (httpMethod == HttpMethods.POST) {
            List<Parameter> parameterList = javaslang.collection.List.of(m.getParameters());
            java.util.List<String> methodParams = new ArrayList<>();
            if (parameterList.size() > 0) {
                methodParams.add("${body}");
                parameterList.tail().forEach(p -> {
                    methodParams.add("${header." + p.getName() + "}");
                });

            }

            params = "(" + String.join(",", methodParams) + ")";
        }

        return m.getName() + params;
    }

    private static RouteDefinition routeToBeanMethod(RestDefinition restDefinition, Object bean, String methodName) {


        RouteDefinition def =  restDefinition.route();

                return def.bean(bean, methodName);


    }


    private static String getPath(Method method) {
        Path methodPathAnnotation = method.getAnnotation(Path.class);

        if (methodPathAnnotation != null) {
            return methodPathAnnotation.value();
        }

        return null;
    }


}
