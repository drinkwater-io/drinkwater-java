package drinkwater.core.internal;

import drinkwater.IDrinkWaterService;
import drinkwater.ServiceRepository;
import drinkwater.ServiceScheme;
import drinkwater.core.CamelContextFactory;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.helper.BeanFactory;
import drinkwater.core.helper.Service;
import drinkwater.rest.RestHelper;
import drinkwater.security.UnauthorizedException;
import drinkwater.trace.Operation;
import javaslang.collection.List;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

import static drinkwater.common.tracing.TraceRouteBuilder.*;
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

    public static RoutesBuilder mapCronRoutes(String groupName, DrinkWaterApplication app, Service service) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                boolean jobActive = service.safeLookupProperty(Boolean.class, "job.activated", true);

                if (jobActive) {

                    Object bean = BeanFactory.createBean(app, service.getConfiguration(), service);

                    String cronExpression = service.getConfiguration().getCronExpression();
                    long interval = service.getConfiguration().getRepeatInterval();

                    cronExpression =
                            service.lookupProperty(service.getConfiguration().getCronExpression() + ":" + cronExpression);

                    interval = service.safeLookupProperty(Long.class, "job.interval", interval);

                    String enpoint = "quartz2://" + groupName + "/" +
                            service.getConfiguration().getServiceName() + "?fireNow=true";

                    if (cronExpression == null) {
                        cronExpression = cronExpression.replaceAll(" ", "+");
                        enpoint += "&cron=" + cronExpression;

                    } else {
                        enpoint += "&trigger.repeatInterval=" + interval;
                    }

                    //camel needs + instead of white space

                    //TODO : check correctness of expression see camel doc here the job must have only one method !!!!
                    from(enpoint).bean(bean);
                }

            }
        };
    }

    public static RouteBuilder mapHttpProxyRoutes(DrinkWaterApplication app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                String frontEndpoint = service.lookupProperty("proxy.endpoint");

                String destinationEndpoint = service.lookupProperty("destination.endpoint");

                if (frontEndpoint == null || destinationEndpoint == null) {
                    throw new RuntimeException("could not find proxy and destination endpoint from config");
                }

                String handlers = getHandlersForJetty(service);
                String handlersConfig = handlers == null ? "":"&handlers="+handlers;

                addExceptionTracing(service, Exception.class, this);

                RouteDefinition choice =
                        from("jetty:" + frontEndpoint + "?matchOnUriPrefix=true" + handlersConfig);

                choice = addServerReceivedTracing(service, choice);
                choice = choice.to("jetty:" + destinationEndpoint + "?bridgeEndpoint=true&amp;throwExceptionOnFailure=true");
                addServerSentTracing(service, choice);
            }
        };
    }

    public static String getHandlersForJetty(Service service) {
        String handlers = service.safeLookupProperty(String.class, "handlers", null);
        if (handlers == null) {
            return null;
        }
        String[] handlersArray = handlers.split(",");

        try {
            for (String handler :
                    handlersArray) {
                Object obj = Class.forName(handler).newInstance();
                CamelContextFactory.registerBean(service.getCamelContext(), handler, obj);
            }
            return handlers;
        } catch (Exception ex) {
            throw new RuntimeException("could not register handler : ", ex);
        }
    }

    //TODO configure request headera nd response size
    public static RouteBuilder mapRoutingRoutes(DrinkWaterApplication app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                String frontEndpoint = endpointFrom(service, service.getConfiguration());

                String handlers = getHandlersForJetty(service);
                String handlersConfig = handlers == null ? "":"&handlers="+handlers;

                RouteDefinition frontRoute = from("jetty:" + frontEndpoint + "?matchOnUriPrefix=true&optionsEnabled=true" + handlersConfig);

                from("direct:SINK_OK_ENDPOINT").transform().constant("OK");

                frontRoute = enableCorsOnRoute(frontRoute, service);

                frontRoute.onException(UnauthorizedException.class)
                        .handled(true)
                        .setHeader("WWW-Authenticate").constant("TOKEN")
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(401))
                        .setBody().constant("Unauthorized");


                ChoiceDefinition choice = frontRoute.choice();

                choice.when(header(Exchange.HTTP_METHOD)
                        .isEqualTo("OPTIONS")).to("direct:SINK_OK_ENDPOINT").endChoice();

                //TODO : fix the way to get the target host
                service.getConfiguration().getRoutingMap().forEach(
                        (key, val) -> {

                            // IServiceConfiguration s = app.getServiceDefinition(val);
                            IDrinkWaterService serviceTemp = app.getDrinkWaterService(val);

                            //Issue #8
                            //TODO better correction needed.
                            String serviceHost = serviceTemp.getConfiguration().getServiceHost();
                            if (serviceTemp.getConfiguration().getScheme() == ServiceScheme.Routeur) {
                                try {
                                    serviceHost = endpointFrom(serviceTemp, serviceTemp.getConfiguration());
                                } catch (Exception e) {
                                    throw new RuntimeException();
                                }
                            }

                            ChoiceDefinition remapChoice = null;

                            if (!"default".equalsIgnoreCase(key)) {
                                remapChoice = choice.when(header(service.getConfiguration().getRoutingHeader()).contains(key));
                            } else {
                                remapChoice = choice.otherwise();
                            }

                            remapChoice = addServerReceivedTracing(service, remapChoice);
                            remapChoice.to("jetty:" + serviceHost + "?bridgeEndpoint=true&throwExceptionOnFailure=true");
                            addServerSentTracing(service, remapChoice);
                        }

                );

            }
        };
    }

    private static RouteDefinition enableCorsOnRoute(RouteDefinition route, Service service) {

        String allowedHeaders = RestHelper.getAllowedCorsheaders(service);
        return route
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .setHeader("Access-Control-Allow-Methods", constant("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH"))
                .setHeader("Access-Control-Allow-Headers", constant(allowedHeaders))
                .setHeader("Allow", constant("GET, OPTIONS, POST, PATCH"));

    }

    public static RouteBuilder mapRestRoutes(DrinkWaterApplication app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                Object bean = BeanFactory.createBean(app, service.getConfiguration(), service);

                RestHelper.buildRestRoutes(this, bean, service);

            }
        };
    }


    //FIXME to many params
    public static RouteBuilder mapBeanClassRoutes(DrinkWaterApplication app, Service service) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                List<Method> methods = javaslang.collection.List.of(service.getConfiguration().getServiceClass().getDeclaredMethods());

                // create an instance of the bean
                Object beanToUse = BeanFactory.createBeanClass(app, service.getConfiguration(), service);

                for (Method m : methods) {
                    if (Modifier.isPublic(m.getModifiers())) {
                        RouteDefinition def = from("direct:" + formatBeanMethodRoute(m));
                        def = addMethodInvokedStartTrace(service, def, Operation.of(m));
                        def.bean(beanToUse, formatBeanEndpointRoute(m), true);
                        addMethodInvokedEndTrace(service, def);

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
