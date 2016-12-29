package drinkwater.core.rest;

import javaslang.Tuple;
import javaslang.collection.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

/**
 * Created by A406775 on 20/12/2016.
 */

class RestRouteBuilder {//extends RouteBuilder {


////    @Inject
////    @ServiceComponent()
//    Instance<Object> _restComponents;
//
//    RestRouteBuilder(Instance<Object> restComponents){
//        _restComponents = restComponents;
//    }
//
//    @Override
//    public void configure() throws Exception {
//
//        logger.fine("configuring rest routes for :" + this.getContext().getName());
//
//        restConfiguration().component("jetty")
//                .host("localhost")
//                .port("8889")
//                .bindingMode(RestBindingMode.json);
//        try {
//
//
////            for(Object obj : _restComponents){
////                buildGetRoutemappings(this, obj);
////                buildPostRoutemappings(this, obj);
////            }
//                List.ofAll(_restComponents)
//                        .map(obj -> buildGetRoutemappings(this, obj));
//                List.ofAll(_restComponents)
//                        .flatMap(obj -> buildPostRoutemappings(this, obj));
//
////            for(Object obj : _restComponents) {
////                rest("/trucks").post()
////                        .type(Class.forName("drinkwater.core.rest.test.models.Truck"))
////                        //.consumes("application/json")
////                        .route()
////                        .bean(obj, "create(${body}, 3)")
////                        .transform()
////                        .constant("[]");
////            }
//
//            Object obj = getRouteCollection();
//
//
//        } catch (Exception ex) {
//            throw ex;
//        }
//    }


    public static List<RouteDefinition> buildPostRoutemappings(RouteBuilder builder, Object bean) {

//        ServiceComponent sc = obj.getClass().getAnnotation(ServiceComponent.class);
//
//        List<RouteDefinition> GetrouteDefinitions =
//                javaslang.collection.List.of(obj.getClass().getDeclaredMethods())
//                        .filter(m -> httpMethodPredicate(m, sc.restPostPrefix()))
//                        .map(m -> Tuple.of(toGetRestDefinition(builder, sc.restPath(), m, HttpMethods.POST), m))
//                        .map((t) -> Tuple.of(t._1, camelMethodBuilder(t._2, HttpMethods.POST)))
//                        .map((t2) -> routeToBean(t2._1, obj, t2._2));

        //FIXME get default or from a config
        String restPOSTPrefixes = "save,create";
        String restPath = "rest";

        List<RouteDefinition> GetrouteDefinitions =
                javaslang.collection.List.of(bean.getClass().getDeclaredMethods())
                        .filter(m -> httpMethodPredicate(m, restPOSTPrefixes))
                        .map(m -> Tuple.of(toGetRestDefinition(builder, restPath, m, HttpMethods.POST, restPOSTPrefixes), m))
                        .map((t) -> Tuple.of(t._1, camelMethodBuilder(t._2, HttpMethods.POST)))
                        .map((t2) -> routeToBean(t2._1, bean, t2._2));

        return GetrouteDefinitions;
    }

    public static List<RouteDefinition> buildGetRoutemappings(RouteBuilder builder, Object bean) {

        //ServiceComponent sc = obj.getClass().getAnnotation(ServiceComponent.class);
//        List<RouteDefinition> GetrouteDefinitions =
//                javaslang.collection.List.of(obj.getClass().getDeclaredMethods())
//                        .filter(m -> httpMethodPredicate(m, sc.restGetPrefix()))
//                        .map(m -> Tuple.of(toGetRestDefinition(builder, sc.restPath(), m, HttpMethods.GET), m))
//                        .map((t) -> Tuple.of(t._1, camelMethodBuilder(t._2,HttpMethods.GET)))
//                        .map((t2) -> routeToBean(t2._1, obj, t2._2));

        //FIXME get default or from a config
        String restGETPrefixes = "get,find";
        String restPath = "rest";

        List<RouteDefinition> GetrouteDefinitions =
                javaslang.collection.List.of(bean.getClass().getDeclaredMethods())
                        .filter(m -> httpMethodPredicate(m, restGETPrefixes))
                        .map(m -> Tuple.of(toGetRestDefinition(builder, restPath, m, HttpMethods.GET, restGETPrefixes), m))
                        .map((t) -> Tuple.of(t._1, camelMethodBuilder(t._2,HttpMethods.GET)))
                        .map((t2) -> routeToBean(t2._1, bean, t2._2));

        return GetrouteDefinitions;
    }

    private static boolean httpMethodPredicate(Method m, String prefixes) {

        return List.of(
                prefixes.toLowerCase().split(","))
                .filter(prefix -> m.getName().toLowerCase().startsWith(prefix))
                .length() > 0;

    }

    private static RestDefinition toPostRestDefinition(RouteBuilder builder, String restPath, Method m) {
        RestDefinition answer = builder.rest(restPath);

        String fromPath = getPath(m);

        if (fromPath == null) {
            fromPath = javaslang.collection.List.of(m.getParameters())
                    .map(p -> "{" + p.getName() + "}").getOrElse("");
        }

        answer.post(fromPath);

        return answer;
    }



    private static RestDefinition toGetRestDefinition(RouteBuilder builder,
                                                      String restPath,
                                                      Method m,
                                                      HttpMethods method,
                                                        String prefixes) {
        RestDefinition answer = builder.rest(restPath);

        String fromPath = getPath(m);

        if(fromPath == null){
            fromPath = List.of(prefixes.toLowerCase().split(","))
                    .filter(prefix -> m.getName().toLowerCase().startsWith(prefix))
                    .map(prefix -> m.getName().replace(prefix, "").toLowerCase())
                    .getOrElse("");
        }

        if(method == HttpMethods.GET) {
            if (fromPath == null || fromPath.isEmpty()) {
                fromPath = javaslang.collection.List.of(m.getParameters())
                        .map(p -> "{" + p.getName() + "}").getOrElse("");
            }
            answer = answer.get(fromPath);
        }
        else if(method == HttpMethods.POST) {
            answer = answer.post(fromPath).type(m.getParameters()[0].getType());
        }
        else if(method == HttpMethods.PUT) {
            answer = answer.put(fromPath);
        }
        else if(method == HttpMethods.DELETE) {
            answer = answer.delete(fromPath);
        }
        else if(method == HttpMethods.PATCH) {
            answer = answer.patch(fromPath);
        }
        else{
            throw new RuntimeException("method currently not supported in Rest Paths : " + method);
        }

        return answer;
    }

    private static String camelMethodBuilder(Method m, HttpMethods method) {
        String params ="";
        if(method == HttpMethods.GET) {
            params = javaslang.collection.List.of(m.getParameters())
                    .map(p -> "${header." + p.getName() + "}")
                    .mkString(",");
            params = "(" + params + ")";
        }

        if(method == HttpMethods.POST) {
            List<Parameter> parameterList = javaslang.collection.List.of(m.getParameters());
            java.util.List<String> methodParams = new ArrayList<>();
            if(parameterList.size() > 0){
                methodParams.add("${body}");
                parameterList.tail().forEach(p -> {
                    methodParams.add("${header." + p.getName() + "}");
                });

            }

            params = "("+ String.join(",", methodParams) + ")";
        }

        return m.getName() +  params ;
    }

    private static RouteDefinition routeToBean(RestDefinition rest, Object bean, String methodName) {
        return rest.route()
                .bean(bean, methodName);
    }


    private static String getPath(Method m) {
        Path methodPathAnnotation = (Path) m.getAnnotation(Path.class);

        if (methodPathAnnotation != null) {
            return methodPathAnnotation.value();
        }

        return null;
    }


}
