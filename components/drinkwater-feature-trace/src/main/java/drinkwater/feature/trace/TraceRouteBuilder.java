package drinkwater.feature.trace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import drinkwater.IBaseEventLogger;
import drinkwater.trace.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static drinkwater.DrinkWaterConstants.*;

public class TraceRouteBuilder extends RouteBuilder {

    public static String ROUTE_CheckFlowIDHeader = "direct:checkFlowHeaderFor";

    public static String ROUTE_serverReceivedEvent = "direct:serverReceivedEventFor";

    public static String ROUTE_serverSentEvent = "direct:serverSentEventFor";

    public static String ROUTE_exceptionEvent = "direct:exceptionEventFor";

    public static String ROUTE_Trace = "direct:traceFor";


    private String componentName;

    public TraceRouteBuilder(String componentName) {
        this.componentName = componentName;
    }

    public static String checkFlowHeaderRouteFor(String componentName){
        return ROUTE_CheckFlowIDHeader + componentName;
    }

    public static String serverReceivedEventRouteFor(String componentName){
        return ROUTE_serverReceivedEvent + componentName;
    }

    public static String serverSentEventRouteFor(String componentName){
        return ROUTE_serverSentEvent + componentName;
    }

    public static String exceptionEventRouteFor(String componentName){
        return ROUTE_exceptionEvent + componentName;
    }

    public static String traceRouteFor(String componentName){
        return ROUTE_Trace + componentName;
    }

    @Override
    public void configure() throws Exception {

        from(checkFlowHeaderRouteFor(componentName)).id("CheckFlowIDHeader").process(exchange -> {
            if (exchange.getIn().getHeader(FlowCorrelationIDKey) == null) {
                exchange.getIn().setHeader(FlowCorrelationIDKey, exchange.getExchangeId());
            }
            exchange.getIn().setHeader(DWTimeStamp, Instant.now());

        });

        //TODO implement undo tracing differently

        //TODO : there can be some reuse here => refactor routes
        from(serverReceivedEventRouteFor(componentName))
                .to(checkFlowHeaderRouteFor(componentName))
                .wireTap("direct:createServerReceivedEventAndTrace" + componentName);

        from(serverSentEventRouteFor(componentName))
                .to(checkFlowHeaderRouteFor(componentName))
                .wireTap("direct:createServerSentEventAndTrace" + componentName);
        from(exceptionEventRouteFor(componentName))
                .to(checkFlowHeaderRouteFor(componentName))
                .wireTap("direct:createExceptionEventAndTrace"+ componentName);



        //server events
        from("direct:createServerReceivedEventAndTrace"+ componentName).process(exchange -> {
            exchange.getIn().setBody(new ServerReceivedEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    applicationNameFrom(exchange),
                    serviceNameFrom(exchange),
                    payloadFrom(exchange)));
        }).to(traceRouteFor(componentName));

        from("direct:createServerSentEventAndTrace"+ componentName).process(exchange -> {
            exchange.getIn().setBody(new ServerSentEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    applicationNameFrom(exchange),
                    serviceNameFrom(exchange),
                    payloadFrom(exchange)));
        }).to(traceRouteFor(componentName));


        from("direct:createExceptionEventAndTrace"+ componentName).process(exchange -> {

            //set track trace in body
            Exception exception = (Exception) exchange.getProperties().get("CamelExceptionCaught");

            Payload payload = Payload.of(
                    methodFrom(exchange),
                    exchange.getIn().getHeaders(),
                    exception);

            exchange.getIn().setBody(new ExceptionEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    applicationNameFrom(exchange),
                    serviceNameFrom(exchange),
                    payload));

        }).to(traceRouteFor(componentName));
    }

    private static String safeMethodName(Operation operation) {
        if (operation != null) {
            return operation.toString();
        }
        return "UNSPECIFIED-OPERATION";
    }

    private static String correlationFrom(Exchange exchange) {
        return (String) exchange.getIn().getHeader(FlowCorrelationIDKey);
    }

    private static String serviceNameFrom(Exchange exchange) {
        return (String) exchange.getIn().getHeader(ComponentName);
    }

    private static String applicationNameFrom(Exchange exchange) {
        return (String) exchange.getIn().getHeader(ApplicationName);
    }

    private static Instant instantFrom(Exchange exchange) {
        return (Instant) exchange.getIn().getHeader(DWTimeStamp);
    }

    private static Payload payloadFrom(Exchange exchange) {
        return Payload.of(methodFrom(exchange), exchange.getIn().getHeaders(), exchange.getIn().getBody());
    }

    private static Operation methodFrom(Exchange exchange) {
        Object operation = exchange.getIn().getHeader(BeanOperationName);
        if (operation == null) {
            return null;
        }

        if (operation instanceof String) {
            return Operation.of((String) operation);
        }
        return (Operation) operation;
    }

//    public static void addExceptionTracing(IDrinkWaterService service,
//                                           Class ExceptionClazz,
//                                           ProcessorDefinition routeDefinition) {
//        if (service.getConfiguration().getIsTraceEnabled()) {
//            routeDefinition.onException(ExceptionClazz).to(ROUTE_exceptionEvent);
//        }
//    }
//
//    public static void addExceptionTracing(IDrinkWaterService service,
//                                           Class ExceptionClazz,
//                                           RouteBuilder routeBuilder) {
//        if (service.getConfiguration().getIsTraceEnabled()) {
//            routeBuilder.onException(ExceptionClazz).to(ROUTE_exceptionEvent);
//        }
//    }
//
//
//
//    public static ProcessorDefinition addServerSentTracing(IDrinkWaterService service,
//                                                       ProcessorDefinition routeDefinition) {
//        ProcessorDefinition answer = routeDefinition;
//        if (!service.getConfiguration().getIsTraceEnabled()) {
//            return answer;
//        }
//
//        return answer.to(ROUTE_serverSentEvent);
//    }
//
//    public static void addClientSentTracing(CamelContext ctx,
//                                            IDrinkWaterService service,
//                                            Method method,
//                                            Object body) {
//        if (service.getConfiguration().getIsTraceEnabled()) {
//            ctx.createProducerTemplate()
//                    .sendBodyAndHeader(ROUTE_clientSentEvent, body,
//                            BeanOperationName, Operation.of(method));
//        }
//    }

//    public static void addClientReceivedTracing(CamelContext ctx,
//                                            IDrinkWaterService service,
//                                            Method method,
//                                            Object body) {
//        if (service.getConfiguration().getIsTraceEnabled()) {
//            ctx.createProducerTemplate()
//                    .sendBodyAndHeader(ROUTE_clientReceivedEvent, body,
//                            BeanOperationName, Operation.of(method));
//        }
//    }

//    public static ProcessorDefinition addServerReceivedTracing(IDrinkWaterService service,
//                                                           RouteDefinition routeDefinition,
//                                                           Method method) {
//        ProcessorDefinition answer = routeDefinition;
//        if (!service.getConfiguration().getIsTraceEnabled()) {
//            return answer;
//        }
//
//        answer = routeDefinition
//                .setHeader(BeanOperationName)
//                .constant(Operation.of(method))
//                .to(ROUTE_serverReceivedEvent);
//
//
//        return answer;
//    }

//    public static RouteDefinition addServerReceivedTracing(IDrinkWaterService service,
//                                                               RouteDefinition routeDefinition) {
//        RouteDefinition answer = routeDefinition;
//        if (!service.getConfiguration().getIsTraceEnabled()) {
//            return answer;
//        }
//
//        answer = routeDefinition
//                .setHeader(BeanOperationName)
//                .method(ExtractHttpMethodFromExchange.class)
//                .to(ROUTE_serverReceivedEvent);
//
//
//        return answer;
//    }

//    public static ChoiceDefinition addServerReceivedTracing(IDrinkWaterService service,
//                                                           ChoiceDefinition routeDefinition) {
//        ChoiceDefinition answer = routeDefinition;
//        if (!service.getConfiguration().getIsTraceEnabled()) {
//            return answer;
//        }
//
//        answer = routeDefinition
//                .setHeader(BeanOperationName)
//                .method(ExtractHttpMethodFromExchange.class)
//                .to(ROUTE_serverReceivedEvent);
//
//
//        return answer;
//    }

//    public static RouteDefinition addMethodInvokedStartTrace(
//            IDrinkWaterService service,
//            RouteDefinition routeDefinition,
//            Operation operation){
//        RouteDefinition answer = routeDefinition;
//        if (!service.getConfiguration().getIsTraceEnabled()) {
//            return answer;
//        }
//
//        answer = routeDefinition
//                .setHeader(BeanOperationName)
//                .constant(operation)
//                .to(ROUTE_MethodInvokedStartEvent);
//
//
//        return answer;
//    }
//
//    public static void addMethodInvokedEndTrace(IDrinkWaterService service,
//                                               RouteDefinition routeDefinition){
//        if (service.getConfiguration().getIsTraceEnabled()) {
//            routeDefinition.to(ROUTE_MethodInvokedEndEvent);
//        }
//    }

}
