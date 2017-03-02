package drinkwater.rest;

import com.mashape.unirest.http.HttpMethod;
import drinkwater.*;
import drinkwater.helper.SocketUtils;
import drinkwater.helper.json.CustomJacksonObjectMapper;
import drinkwater.helper.reflect.ReflectHelper;
import drinkwater.rest.fileupload.FileUploadProcessor;
import drinkwater.rest.security.SecurityProcessor;
import drinkwater.security.UnauthorizedException;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestPropertyDefinition;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static drinkwater.common.tracing.TraceRouteBuilder.*;
import static drinkwater.helper.StringUtils.startsWithOneOf;
import static java.util.Collections.singletonList;
import static org.apache.camel.builder.Builder.constant;

/**
 * Created by A406775 on 30/12/2016.
 */
public class RestHelper {



    private static Map<HttpMethod, String[]> prefixesMap = new HashMap<>();

    //FIXME : get it from some config?
    static {
        prefixesMap.put(HttpMethod.GET, new String[]{"get", "find", "check"});
        prefixesMap.put(HttpMethod.POST, new String[]{"new", "save", "create", "set", "route", "clear", "add"});
        prefixesMap.put(HttpMethod.DELETE, new String[]{"delete", "remove"});
        prefixesMap.put(HttpMethod.PUT, new String[]{"update", "remove"});
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


    public static Tuple2<RestDefinition, Method> buildRestRoute(RouteBuilder builder, Method method, ITracer tracer) {

        //builder.interceptFrom().setHeader(BeanOperationName).constant(method).to(ROUTE_serverReceivedEvent);

        HttpMethod httpMethod = httpMethodFor(method);

        String restPath = restPathFor(method);

        RestDefinition restDefinition =
                toRestdefinition(builder, method, httpMethod, restPath);

        //restDefinition.enableCORS(true);

        return Tuple.of(restDefinition, method);

    }


    public static String formatMethod(Method m) {
        return m.getDeclaringClass().getSimpleName() + "." + m.getName();
    }

    private static String host(IPropertyResolver propertiesResolver) throws Exception {
        return propertiesResolver.lookupProperty(RestService.REST_HOST_KEY + ":0.0.0.0");
    }

    private static String port(IDrinkWaterService propertiesResolver) throws Exception {

       String port = propertiesResolver.addProperty(RestService.REST_PORT_KEY, (key) -> SocketUtils.freePort() + "");

       return port;

//        String port = propertiesResolver.safeLookupProperty(String.class, RestService.REST_PORT_KEY, null);
//
//        SocketUtils.freePort();
//
//        String portKey = RestService.REST_PORT_KEY + ":8889";
//        return propertiesResolver.lookupProperty(portKey);
    }

    private static String sinkPort(IPropertyResolver propertiesResolver) throws Exception {
        String portKey = RestService.REST_SINK_PORT_KEY + ":8890";
        return propertiesResolver.lookupProperty(portKey);
    }

    private static String context(IPropertyResolver propertiesResolver, IServiceConfiguration config) throws Exception {
        return propertiesResolver.lookupProperty(RestService.REST_CONTEXT_KEY + ":" + config.getServiceName());
    }



    public static String endpointFrom(IDrinkWaterService service) throws Exception {

        String serviceHost = "";
            serviceHost = "http://" + host(service) + ":" + port(service) + "/" + context(service, service.getConfiguration());
        return serviceHost;

    }


    public static String getAllowedCorsheaders(IPropertyResolver propertiesResolver){
        String genericHeaders = " Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization, x-filename";
        String additionalHeaders = propertiesResolver.safeLookupProperty(String.class,  RestService.CORS_CONTEXT_KEY,"");
        additionalHeaders = (additionalHeaders.isEmpty() ? "" : "," + additionalHeaders);

        String allowedHeadersList = genericHeaders + additionalHeaders;

        return allowedHeadersList;

    }

    public static void buildRestRoutes(RouteBuilder builder, Object bean,
                                       IDrinkWaterService drinkWaterService) {

        //check this for cors problems
        //http://camel.465427.n5.nabble.com/Workaround-with-REST-DSL-to-avoid-HTTP-method-not-allowed-405-td5771508.html
        try {

            String serviceHost = endpointFrom(drinkWaterService);
            drinkWaterService.getConfiguration().setServiceHost(serviceHost);

            RestPropertyDefinition corsAllowedHeaders = new RestPropertyDefinition();
            corsAllowedHeaders.setKey("Access-Control-Allow-Headers");
            corsAllowedHeaders.setValue(getAllowedCorsheaders(drinkWaterService));

            // builder.getContext().getDataFormats();
            RestConfigurationDefinition restConfig =
                    builder.restConfiguration()
                            .component("jetty")
                            .enableCORS(true)
                            .scheme("http")
                            .host(host(drinkWaterService))
                            .port(port(drinkWaterService))
                            .contextPath(context(drinkWaterService, drinkWaterService.getConfiguration()))
                            .bindingMode(RestBindingMode.json)
                            .jsonDataFormat("json-drinkwater");

            restConfig.setCorsHeaders(singletonList(corsAllowedHeaders));

        } catch (Exception ex) {
            throw new RuntimeException("could not configure the rest service correctly", ex);
        }

        javaslang.collection
                .List.of(ReflectHelper.getPublicDeclaredMethods(bean.getClass()))
                .map(method -> buildRestRoute(builder, method, drinkWaterService.getTracer()))
                .map(tuple -> routeToBeanMethod(tuple._1, bean, tuple._2, drinkWaterService));
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


    public static RestDefinition toRestdefinition(RouteBuilder builder,
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
        //This route necessary for the RestBindings of camel
        if (method.getParameters().length > 0) {

            Class type = method.getParameters()[0].getType();
            if (isMultipartBody(method)) {
                rd.bindingMode(RestBindingMode.off);
                rd.outType(method.getReturnType().getClass());
            }
            rd = rd.type(type);
        }
        return rd;
    }


    private static boolean isMultipartBody(Method method) {

        if (method.getParameters().length > 0) {
            Class type = method.getParameters()[0].getType();
            if (type.isAssignableFrom(InputStream.class)) {
                return true;
            }
        }
        return false;
    }

    public static String camelMethodBuilder(Method m) {

        MethodToRestParameters methodtoRoute = new MethodToRestParameters(m);

        return methodtoRoute.exchangeToBean();

    }

    private static String methodSignature(Method m) {

        MethodToRestParameters methodtoRoute = new MethodToRestParameters(m);

        return methodtoRoute.exchangeToBean();

    }

    private static ProcessorDefinition routeToBeanMethod(
            RestDefinition restDefinition,
            Object bean,
            Method method,
            IDrinkWaterService iDrinkWaterService) {
        String camelMethod = camelMethodBuilder(method);

        ProcessorDefinition routeDefinition =
                addServerReceivedTracing(iDrinkWaterService, restDefinition.route(), method);

        if(iDrinkWaterService.safeLookupProperty(Boolean.class,
                DrinkWaterPropertyConstants.Authenticate_Enabled,
                true)){
            routeDefinition.process(new SecurityProcessor());
        }

        routeDefinition.onException(UnauthorizedException.class)
                .handled(true)
                .setHeader("WWW-Authenticate").constant("TOKEN")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(401))
                .setBody().constant("Unauthorized");

        routeDefinition.setHeader("Access-Control-Allow-Headers", constant("*"));
        routeDefinition.setHeader("Access-Control-Allow-Origin", constant("*"));

        addExceptionTracing(iDrinkWaterService, Exception.class, routeDefinition);


        //TODO create own process
        if (isMultipartBody(method)) {
            routeDefinition.process(new FileUploadProcessor());
        }

        routeDefinition = routeDefinition.bean(bean, camelMethod);
        //if binding is off and returns an object, then serialize as json
        if (isMultipartBody(method) && hasObjectReturnType(method)) {
            routeDefinition.process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
                    String s = mapper.writeValueAsString(exchange.getIn().getBody());
                    exchange.getIn().setBody(s);
                }
            });
        }

        return addServerSentTracing(iDrinkWaterService, routeDefinition);
    }

    private static boolean hasObjectReturnType(Method method) {
        if (method.getReturnType() == String.class) {
            return false;
        }
        return method.getReturnType() != Void.class;
    }


    private static String getPathFromAnnotation(Method method) {
        Path methodPathAnnotation = method.getAnnotation(Path.class);

        if (methodPathAnnotation != null) {
            return methodPathAnnotation.value();
        }

        return null;
    }

}
