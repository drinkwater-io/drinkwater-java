package drinkwater.rest;

import com.mashape.unirest.http.HttpMethod;
import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;
import drinkwater.helper.reflect.ReflectHelper;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static drinkwater.helper.StringHelper.startsWithOneOf;

/**
 * Created by A406775 on 30/12/2016.
 */
public class RestHelper {

    private static Map<HttpMethod, String[]> prefixesMap = new HashMap<>();

    //FIXME : get it from some config?
    static {
        prefixesMap.put(HttpMethod.GET, new String[]{"get", "find", "check"});
        prefixesMap.put(HttpMethod.POST, new String[]{"save", "create", "set"});
        prefixesMap.put(HttpMethod.DELETE, new String[]{"delete", "remove", "clear"});
    }

    public static HttpMethod httpMethodFor(Method method) {
        return List.ofAll(prefixesMap.entrySet())
                .filter(prefix -> startsWithOneOf(method.getName(), prefix.getValue()))
                .map(entryset -> entryset.getKey())
                .getOrElse(HttpMethod.OPTIONS);
    }


    public static String restPathFor(Method method) {
        HttpMethod httpMethod = httpMethodFor(method);

        String restPath = restPath(method, httpMethod);

        return restPath;
    }


    public static Tuple2<RestDefinition, String> buildRestRoute(RouteBuilder builder, Method method) {

        HttpMethod httpMethod = httpMethodFor(method);

        String restPath = restPathFor(method);

        RestDefinition restDefinition =
                toRestdefinition(builder, method, httpMethod, restPath);

        String camelMethod = camelMethodBuilder(method, httpMethod);

        return Tuple.of(restDefinition, camelMethod);

    }

    public static String host(IPropertyResolver propertiesResolver) throws Exception {
        return propertiesResolver.lookupProperty(RestService.REST_HOST_KEY + ":localhost");
    }

    public static String port(IPropertyResolver propertiesResolver) throws Exception {
        return propertiesResolver.lookupProperty(RestService.REST_PORT_KEY + ":8889");
    }

    public static String context(IPropertyResolver propertiesResolver, IServiceConfiguration config) throws Exception {
        return propertiesResolver.lookupProperty(RestService.REST_CONTEXT_KEY + ":" + config.getServiceClass().getName().toLowerCase());
    }

    public static void buildRestRoutes(RouteBuilder builder, Object bean, IPropertyResolver propertiesResolver, IServiceConfiguration config) {

        try {
            builder.restConfiguration().component("jetty")
                    .host(host(propertiesResolver))
                    .port(port(propertiesResolver))
                    .contextPath(context(propertiesResolver, config))
                    .bindingMode(RestBindingMode.json);
        } catch (Exception ex) {
            throw new RuntimeException("could not configure the rest service correctly", ex);
        }

        javaslang.collection.List.of(ReflectHelper.getPublicDeclaredMethods(bean.getClass()))
                .map(method -> buildRestRoute(builder, method))
                .map(tuple -> routeToBeanMethod(tuple._1, bean, tuple._2));
    }

    private static String restPath(Method method, HttpMethod httpMethod) {
        if (httpMethod == HttpMethod.OPTIONS) {
            return "";
        }
        String fromPath = getPath(method);

        if (fromPath == null) {
            fromPath = List.of(prefixesMap.get(httpMethod))
                    .filter(prefix -> method.getName().toLowerCase().startsWith(prefix))
                    .map(prefix -> method.getName().replace(prefix, "").toLowerCase())
                    .getOrElse("");
        }

        if (httpMethod == HttpMethod.GET) {
            if (fromPath == null || fromPath.isEmpty()) {
                fromPath = javaslang.collection.List.of(method.getParameters())
                        .map(p -> "{" + p.getName() + "}").getOrElse("");
            }
        }

        return fromPath;
    }


    private static RestDefinition toRestdefinition(RouteBuilder builder,
                                                   Method method,
                                                   HttpMethod httpMethod,
                                                   String restPath) {
        RestDefinition answer = builder.rest();

        String fromPath = restPath;

        if (httpMethod == HttpMethod.GET) {
            answer = answer.get(fromPath);
        } else if (httpMethod == HttpMethod.POST) {
            answer = answer.post(fromPath).type(method.getParameters()[0].getType());
        } else if (httpMethod == HttpMethod.PUT) {
            answer = answer.put(fromPath);
        } else if (httpMethod == HttpMethod.DELETE) {
            answer = answer.delete(fromPath);
        } else if (httpMethod == HttpMethod.PATCH) {
            answer = answer.patch(fromPath);
        } else {
            throw new RuntimeException("method currently not supported in Rest Paths : " + httpMethod);
        }

        return answer;
    }

    private static String camelMethodBuilder(Method m, HttpMethod httpMethod) {
        String params = "";
        if (httpMethod == HttpMethod.GET) {
            params = javaslang.collection.List.of(m.getParameters())
                    .map(p -> "${header." + p.getName() + "}")
                    .mkString(",");
            params = "(" + params + ")";
        }

        if (httpMethod == HttpMethod.POST) {
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
        RouteDefinition def = restDefinition.route();

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
