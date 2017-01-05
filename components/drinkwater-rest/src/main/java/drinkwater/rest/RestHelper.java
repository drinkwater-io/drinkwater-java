package drinkwater.rest;

import com.mashape.unirest.http.HttpMethod;
import drinkwater.IDrinkWaterService;
import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;
import drinkwater.ITracer;
import drinkwater.helper.reflect.ReflectHelper;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static drinkwater.DrinkWaterConstants.ROUTE_serverReceivedEvent;
import static drinkwater.DrinkWaterConstants.ROUTE_serverSentEvent;
import static drinkwater.helper.StringHelper.startsWithOneOf;

/**
 * Created by A406775 on 30/12/2016.
 */
public class RestHelper {

    private static Logger logger = Logger.getLogger(RestHelper.class.getName());

    private static Map<HttpMethod, String[]> prefixesMap = new HashMap<>();

    //FIXME : get it from some config?
    static {
        prefixesMap.put(HttpMethod.GET, new String[]{"get", "find", "check"});
        prefixesMap.put(HttpMethod.POST, new String[]{"save", "create", "set", "is", "clear", "add"});
        prefixesMap.put(HttpMethod.DELETE, new String[]{"delete", "remove"});
    }

    public static HttpMethod httpMethodFor(Method method) {

        HttpMethod defaultHttpMethod = HttpMethod.GET;

        drinkwater.rest.HttpMethod methodAsAnnotation = method.getDeclaredAnnotation(drinkwater.rest.HttpMethod.class);

        if (methodAsAnnotation != null) {
            return mapToUnirestHttpMethod(methodAsAnnotation);
        }

        return List.ofAll(prefixesMap.entrySet())
                .filter(prefix -> startsWithOneOf(method.getName(), prefix.getValue()))
                .map(entryset -> entryset.getKey())
                .getOrElse(defaultHttpMethod);
    }

    public static HttpMethod mapToUnirestHttpMethod(drinkwater.rest.HttpMethod methodAsAnnotation) {
        switch (methodAsAnnotation.value().toUpperCase()) {
            case "GET":
                return HttpMethod.GET;
            case "POST":
                return HttpMethod.POST;
            case "DELETE":
                return HttpMethod.DELETE;
            case "PUT":
                return HttpMethod.PUT;
            case "PATCH":
                return HttpMethod.PATCH;
            default:
                throw new RuntimeException(String.format("could not map correct http method : %s", methodAsAnnotation.value()));
        }
    }


    public static String restPathFor(Method method) {
        HttpMethod httpMethod = httpMethodFor(method);

        String restPath = restPath(method, httpMethod);

        return restPath;
    }


    public static Tuple2<RestDefinition, String> buildRestRoute(RouteBuilder builder, Method method, ITracer tracer) {

        builder.interceptFrom().to(ROUTE_serverReceivedEvent);

        HttpMethod httpMethod = httpMethodFor(method);

        String restPath = restPathFor(method);

        RestDefinition restDefinition =
                toRestdefinition(builder, method, httpMethod, restPath);


        String camelMethod = camelMethodBuilder(method);

        return Tuple.of(restDefinition, camelMethod);

    }

    public static String formatMethod(Method m) {
        return m.getDeclaringClass().getSimpleName() + "." + m.getName();
    }

    public static String host(IPropertyResolver propertiesResolver) throws Exception {
        return propertiesResolver.lookupProperty(RestService.REST_HOST_KEY + ":0.0.0.0");
    }

    public static String port(IPropertyResolver propertiesResolver) throws Exception {
        String portKey = RestService.REST_PORT_KEY + ":8889";
        return propertiesResolver.lookupProperty(portKey);
    }

    public static String context(IPropertyResolver propertiesResolver, IServiceConfiguration config) throws Exception {
        return propertiesResolver.lookupProperty(RestService.REST_CONTEXT_KEY + ":" + config.getServiceName());
    }

    public static void buildRestRoutes(RouteBuilder builder, Object bean,
                                       IPropertyResolver propertiesResolver,
                                       IDrinkWaterService config) {

        try {
            // builder.getContext().getDataFormats();
            builder.restConfiguration().component("jetty")
                    .host(host(propertiesResolver))
                    .port(port(propertiesResolver))
                    .contextPath(context(propertiesResolver, config.getConfiguration()))
                    .bindingMode(RestBindingMode.json)
                    .jsonDataFormat("json-drinkwater")
            ;
        } catch (Exception ex) {
            throw new RuntimeException("could not configure the rest service correctly", ex);
        }

        javaslang.collection.List.of(ReflectHelper.getPublicDeclaredMethods(bean.getClass()))
                .map(method -> buildRestRoute(builder, method, config.getTracer()))
                .map(tuple -> routeToBeanMethod(tuple._1, bean, tuple._2, config.getTracer()));
    }

    private static String restPath(Method method, HttpMethod httpMethod) {
        if (httpMethod == HttpMethod.OPTIONS) {
            return "";
        }
        String fromPath = getPathFromAnnotation(method);

        if (fromPath == null || fromPath.isEmpty()) {
            fromPath = List.of(prefixesMap.get(httpMethod))
                    .filter(prefix -> method.getName().toLowerCase().startsWith(prefix))
                    .map(prefix -> method.getName().replace(prefix, "").toLowerCase())
                    .getOrElse("");

            //if still empty
            if (fromPath.isEmpty()) {
                fromPath = method.getName();
            }
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
            answer = answer.post(fromPath);
        } else if (httpMethod == HttpMethod.PUT) {
            answer = answer.put(fromPath);
        } else if (httpMethod == HttpMethod.DELETE) {
            answer = answer.delete(fromPath);
        } else if (httpMethod == HttpMethod.PATCH) {
            answer = answer.patch(fromPath);
        } else {
            throw new RuntimeException("method currently not supported in Rest Paths : " + httpMethod);
        }

        answer = setBodyType(answer, method);

        return answer;
    }

    private static RestDefinition setBodyType(RestDefinition rd, Method method) {
        //This is necessary for the RestBindings of camel
        if (method.getParameters().length > 0) {
            rd = rd.type(method.getParameters()[0].getType());
        }
        return rd;
    }


    private static String camelMethodBuilder(Method m) {

        MethodToRestParameters methodtoRoute = new MethodToRestParameters(m);

        return methodtoRoute.exchangeToBean();

    }

    private static String methodSignature(Method m) {

        MethodToRestParameters methodtoRoute = new MethodToRestParameters(m);

        return methodtoRoute.exchangeToBean();

    }

    private static RouteDefinition routeToBeanMethod(RestDefinition restDefinition, Object bean, String camelFormattedMethodName, ITracer tracer) {
        RouteDefinition def = restDefinition.route();

        return def.bean(bean, camelFormattedMethodName).wireTap(ROUTE_serverSentEvent).end();
    }


    private static String getPathFromAnnotation(Method method) {
        Path methodPathAnnotation = method.getAnnotation(Path.class);

        if (methodPathAnnotation != null) {
            return methodPathAnnotation.value();
        }

        return null;
    }

}
