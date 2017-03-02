package drinkwater.common.tracing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import drinkwater.IBaseEventLogger;
import drinkwater.IDrinkWaterService;
import drinkwater.trace.*;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.Instant;

import static drinkwater.DrinkWaterConstants.*;

public class TraceRouteBuilder extends RouteBuilder {

    private static String ROUTE_CheckFlowIDHeader = "direct:checkFlowHeader";

    private static String ROUTE_serverReceivedEvent = "direct:serverReceivedEvent";

    private static String ROUTE_serverSentEvent = "direct:serverSentEvent";

    private static String ROUTE_exceptionEvent = "direct:exceptionEvent";

    private static String ROUTE_clientReceivedEvent = "direct:clientReceivedEvent";

    private static String ROUTE_clientSentEvent = "direct:clientSentEvent";

    private static String ROUTE_MethodInvokedStartEvent = "direct:methodInvokedStart";

    private static String ROUTE_MethodInvokedEndEvent = "direct:methodInvokedEnd";

    private static String ROUTE_operationEvent = "direct:operationEvent";

    private static String ROUTE_trace = "vm:trace";

    @JsonIgnore
    private static Logger logger = LoggerFactory.getLogger(TraceRouteBuilder.class);

    private IDrinkWaterService service;

    private boolean isTracingEnabled;

    public TraceRouteBuilder(IDrinkWaterService service, boolean isTracingEnabled) {
        this.service = service;
        this.isTracingEnabled = isTracingEnabled;
    }

    @Override
    public void configure() throws Exception {

        if (!isTracingEnabled) {
            return;
        }

        IBaseEventLogger emptyLogger = new IBaseEventLogger() {
            @Override
            public void logEvent(BaseEvent event) {
                logger.debug(event.toString());
            }
        };

        from(ROUTE_CheckFlowIDHeader).id("CheckFlowIDHeader").process(exchange -> {
            if (exchange.getIn().getHeader(FlowCorrelationIDKey) == null) {
                exchange.getIn().setHeader(FlowCorrelationIDKey, exchange.getExchangeId());
            }
            exchange.getIn().setHeader(DWTimeStamp, Instant.now());

        });

        //TODO implement undo tracing differently

        //TODO : there can be some reuse here => refactor routes
        from(ROUTE_serverReceivedEvent)
                .to(ROUTE_CheckFlowIDHeader)
                .wireTap("direct:createServerReceivedEventAndTrace").id("async-createServerReceivedEventAndTrace");

        from(ROUTE_serverSentEvent)
                .to(ROUTE_CheckFlowIDHeader)
                .wireTap("direct:createServerSentEventAndTrace").id("async-createServerSentEventAndTrace");

        from(ROUTE_clientReceivedEvent)
                .to(ROUTE_CheckFlowIDHeader)
                .wireTap("direct:createClientReceivedEventAndTrace").id("async-createClientReceivedEventAndTrace");

        from(ROUTE_clientSentEvent)
                .to(ROUTE_CheckFlowIDHeader)
                .wireTap("direct:createClientSentEventAndTrace").id("async-createClientSentEventAndTrace");

        from(ROUTE_exceptionEvent)
                .to(ROUTE_CheckFlowIDHeader)
                .wireTap("direct:createExceptionEventAndTrace").id("async-createExceptionEventAndTrace");


        from(ROUTE_MethodInvokedStartEvent)
                .to(ROUTE_CheckFlowIDHeader)
                .wireTap("direct:createMISEventAndTrace").id("async-createMISEventAndTrace");

        from(ROUTE_MethodInvokedEndEvent)
                .to(ROUTE_CheckFlowIDHeader)
                .wireTap("direct:createMIEEventAndTrace").id("async-createMIEEventAndTrace");

        from("direct:emptyLogger").bean(emptyLogger, "logEvent(${body})");

        String tracingRoute = ROUTE_trace;

        if (!isTracingEnabled) {
            tracingRoute = "direct:emptyLogger";
        }

        //server events
        from("direct:createServerReceivedEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new ServerReceivedEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service.getApplicationName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        from("direct:createServerSentEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new ServerSentEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service.getApplicationName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        //client events
        from("direct:createClientReceivedEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new ClientReceivedEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service.getApplicationName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        from("direct:createClientSentEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new ClientSentEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service.getApplicationName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        //method invocation events
        from("direct:createMISEventAndTrace").process(exchange -> {

            exchange.getIn().setBody(new MethodInvocationStartEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service.getApplicationName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        from("direct:createMIEEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new MethodInvocationEndEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service.getApplicationName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        //method invocation events
        from("direct:createExceptionEventAndTrace").process(exchange -> {

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
                    service.getApplicationName(),
                    service.getConfiguration().getServiceName(),
                    payload));

        }).to(tracingRoute);
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

    public static void addExceptionTracing(IDrinkWaterService service,
                                           Class ExceptionClazz,
                                           ProcessorDefinition routeDefinition) {
        if (service.getConfiguration().getIsTraceEnabled()) {
            routeDefinition.onException(ExceptionClazz).to(ROUTE_exceptionEvent);
        }
    }

    public static void addExceptionTracing(IDrinkWaterService service,
                                           Class ExceptionClazz,
                                           RouteBuilder routeBuilder) {
        if (service.getConfiguration().getIsTraceEnabled()) {
            routeBuilder.onException(ExceptionClazz).to(ROUTE_exceptionEvent);
        }
    }



    public static ProcessorDefinition addServerSentTracing(IDrinkWaterService service,
                                                       ProcessorDefinition routeDefinition) {
        ProcessorDefinition answer = routeDefinition;
        if (!service.getConfiguration().getIsTraceEnabled()) {
            return answer;
        }

        return answer.to(ROUTE_serverSentEvent);
    }

    public static void addClientSentTracing(CamelContext ctx,
                                            IDrinkWaterService service,
                                            Method method,
                                            Object body) {
        if (service.getConfiguration().getIsTraceEnabled()) {
            ctx.createProducerTemplate()
                    .sendBodyAndHeader(ROUTE_clientSentEvent, body,
                            BeanOperationName, Operation.of(method));
        }
    }

    public static void addClientReceivedTracing(CamelContext ctx,
                                            IDrinkWaterService service,
                                            Method method,
                                            Object body) {
        if (service.getConfiguration().getIsTraceEnabled()) {
            ctx.createProducerTemplate()
                    .sendBodyAndHeader(ROUTE_clientReceivedEvent, body,
                            BeanOperationName, Operation.of(method));
        }
    }

    public static ProcessorDefinition addServerReceivedTracing(IDrinkWaterService service,
                                                           RouteDefinition routeDefinition,
                                                           Method method) {
        ProcessorDefinition answer = routeDefinition;
        if (!service.getConfiguration().getIsTraceEnabled()) {
            return answer;
        }

        answer = routeDefinition
                .setHeader(BeanOperationName)
                .constant(Operation.of(method))
                .to(ROUTE_serverReceivedEvent);


        return answer;
    }

    public static RouteDefinition addServerReceivedTracing(IDrinkWaterService service,
                                                               RouteDefinition routeDefinition) {
        RouteDefinition answer = routeDefinition;
        if (!service.getConfiguration().getIsTraceEnabled()) {
            return answer;
        }

        answer = routeDefinition
                .setHeader(BeanOperationName)
                .method(ExtractHttpMethodFromExchange.class)
                .to(ROUTE_serverReceivedEvent);


        return answer;
    }

    public static ChoiceDefinition addServerReceivedTracing(IDrinkWaterService service,
                                                           ChoiceDefinition routeDefinition) {
        ChoiceDefinition answer = routeDefinition;
        if (!service.getConfiguration().getIsTraceEnabled()) {
            return answer;
        }

        answer = routeDefinition
                .setHeader(BeanOperationName)
                .method(ExtractHttpMethodFromExchange.class)
                .to(ROUTE_serverReceivedEvent);


        return answer;
    }

    public static RouteDefinition addMethodInvokedStartTrace(
            IDrinkWaterService service,
            RouteDefinition routeDefinition,
            Operation operation){
        RouteDefinition answer = routeDefinition;
        if (!service.getConfiguration().getIsTraceEnabled()) {
            return answer;
        }

        answer = routeDefinition
                .setHeader(BeanOperationName)
                .constant(operation)
                .to(ROUTE_MethodInvokedStartEvent);


        return answer;
    }

    public static void addMethodInvokedEndTrace(IDrinkWaterService service,
                                               RouteDefinition routeDefinition){
        if (service.getConfiguration().getIsTraceEnabled()) {
            routeDefinition.to(ROUTE_MethodInvokedEndEvent);
        }
    }

}
