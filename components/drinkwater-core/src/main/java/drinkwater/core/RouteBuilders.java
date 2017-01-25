package drinkwater.core;

import drinkwater.IServiceConfiguration;
import drinkwater.core.helper.BeanFactory;
import drinkwater.core.helper.DefaultPropertyResolver;
import drinkwater.core.helper.ExtractHttpMethodFromExchange;
import drinkwater.core.helper.Service;
import drinkwater.rest.RestHelper;
import drinkwater.trace.Operation;
import javaslang.collection.List;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

import static drinkwater.DrinkWaterConstants.*;
import static drinkwater.rest.RestHelper.endpointFrom;
import static org.apache.camel.builder.Builder.constant;

/**
 * Created by A406775 on 27/12/2016.
 */
public class RouteBuilders {

    public static RoutesBuilder mapBeanRoutes(ServiceRepository serviceRepository,
                                              Service service) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                List<Method> methods = javaslang.collection.List.of(service.getConfiguration().getServiceClass().getDeclaredMethods());

                // create an instance of the bean
                Object beanToUse = service.getConfiguration().getTargetBean();

                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers())) {
                        from("direct:" + formatBeanMethodRoute(m))
                                .bean(beanToUse, formatBeanEndpointRoute(m), true);
                    }
                }
            }
        };
    }

    public static RoutesBuilder mapCronRoutes(String groupName, ServiceRepository app, Service service) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                String enpoint = "quartz2://" + groupName + "/" +
                        service.getConfiguration().getServiceName() + "?fireNow=true";

                Object bean = BeanFactory.createBean(app, service.getConfiguration(), service);

                String cronExpression = service.getConfiguration().getCronExpression();

                cronExpression =
                        service.lookupProperty(service.getConfiguration().getCronExpression() + ":" + cronExpression);

                if (cronExpression == null) {
                    cronExpression = cronExpression.replaceAll(" ", "+");
                    enpoint += "&cron=" + cronExpression;

                } else {
                    enpoint += "&trigger.repeatInterval=" + service.getConfiguration().getRepeatInterval();
                }

                //camel needs + instead of white space

                //TODO : check correctness of expression see camel doc here the job must have only one method !!!!
                from(enpoint).bean(bean);

            }
        };
    }

    public static RouteBuilder mapHttpProxyRoutes(ServiceRepository app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                String frontEndpoint = service.lookupProperty(
                        service.getConfiguration().getServiceName() + ".proxy.endpoint");

                String destinationEndpoint = service.lookupProperty(
                        service.getConfiguration().getServiceName() + ".destination.endpoint");

                if (frontEndpoint == null || destinationEndpoint == null) {
                    throw new RuntimeException("could not find proxy and destination endpoint from config");
                }

                onException(Exception.class).to(ROUTE_exceptionEvent);

                RouteDefinition choice =
                        from("jetty:" + frontEndpoint + "?matchOnUriPrefix=true").id("front-proxy-received-" + service.getConfiguration().getServiceName())
                                .setHeader(BeanOperationName).method(ExtractHttpMethodFromExchange.class).id("setOperationNameInHeader")
                                .to(ROUTE_serverReceivedEvent).id("trace-received")
                                .to("jetty:" + destinationEndpoint + "?bridgeEndpoint=true&amp;throwExceptionOnFailure=true")
                                .id("front-proxy-reply-" + service.getConfiguration().getServiceName())
                                .to(ROUTE_serverSentEvent).id("trace-sent");

            }
        };
    }


    public static RouteBuilder mapRoutingRoutes(ServiceRepository app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                String frontEndpoint = endpointFrom(new DefaultPropertyResolver(service), service.getConfiguration());

                RouteDefinition frontRoute = from("jetty:" + frontEndpoint + "?matchOnUriPrefix=true&optionsEnabled=true");

                frontRoute = enableCorsOnRoute(frontRoute);

                ChoiceDefinition choice = frontRoute.choice();

                //TODO : fix the way to get the target host
                service.getConfiguration().getRoutingMap().forEach(
                        (key, val) -> {

                            IServiceConfiguration s = app.getServiceDefinition(val);

                            choice.when(header(service.getConfiguration().getRoutingHeader()).isEqualTo(key))
                                    .setHeader(BeanOperationName).method(ExtractHttpMethodFromExchange.class).id("setOperationNameInHeader")
                                    .to(ROUTE_serverReceivedEvent).id("trace-received")
                                    .to("jetty:" + s.getServiceHost() + "?bridgeEndpoint=true&throwExceptionOnFailure=true")
                                    .to(ROUTE_serverSentEvent).id("trace-sent");

                        }

                );

            }
        };
    }

    private static RouteDefinition enableCorsOnRoute(RouteDefinition route) {
        return route
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .setHeader("Access-Control-Allow-Methods", constant("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH"))
                .setHeader("Access-Control-Allow-Headers", constant("Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,ngba_origin,ngba_user"))
                .setHeader("Allow", constant("GET, OPTIONS, POST, PATCH"));

    }

    public static RouteBuilder mapRestRoutes(ServiceRepository app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                Object bean = BeanFactory.createBean(app, service.getConfiguration(), service);

                RestHelper.buildRestRoutes(this, bean, new DefaultPropertyResolver(service), service);

            }
        };
    }


    //FIXME to many params
    public static RouteBuilder mapBeanClassRoutes(ServiceRepository app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                List<Method> methods = javaslang.collection.List.of(service.getConfiguration().getServiceClass().getDeclaredMethods());

                // create an instance of the bean
                Object beanToUse = BeanFactory.createBeanClass(app, service.getConfiguration(), service);

                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers())) {
                        from("direct:" + formatBeanMethodRoute(m))
                                .setHeader(BeanOperationName).constant(Operation.of(m))
                                .wireTap(ROUTE_MethodInvokedStartEvent).end()
                                .bean(beanToUse, formatBeanEndpointRoute(m), true)
                                .to(ROUTE_MethodInvokedEndEvent);
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
