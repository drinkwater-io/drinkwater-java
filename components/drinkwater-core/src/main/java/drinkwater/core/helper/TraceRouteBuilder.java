package drinkwater.core.helper;

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

    @JsonIgnore
    private static Logger logger = LoggerFactory.getLogger(TraceRouteBuilder.class);

    private Service service;

    private boolean isTracingEnabled;

    public TraceRouteBuilder(Service service, boolean isTracingEnabled) {
        this.service = service;
        this.isTracingEnabled = isTracingEnabled;
    }

    @Override
    public void configure() throws Exception {

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
                    service._dwa.getPropertiesDefaultName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        from("direct:createServerSentEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new ServerSentEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service._dwa.getPropertiesDefaultName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        //client events
        from("direct:createClientReceivedEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new ClientReceivedEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service._dwa.getPropertiesDefaultName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        from("direct:createClientSentEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new ClientSentEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service._dwa.getPropertiesDefaultName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        //method invocation events
        from("direct:createMISEventAndTrace").process(exchange -> {

            exchange.getIn().setBody(new MethodInvocationStartEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service._dwa.getPropertiesDefaultName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        from("direct:createMIEEventAndTrace").process(exchange -> {
            exchange.getIn().setBody(new MethodInvocationEndEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service._dwa.getPropertiesDefaultName(),
                    service.getConfiguration().getServiceName(),
                    payloadFrom(exchange)));
        }).to(tracingRoute);

        //method invocation events
        from("direct:createExceptionEventAndTrace").process(exchange -> {

            //set track trace in body
            Exception exception = (Exception)exchange.getProperties().get("CamelExceptionCaught");

            Payload payload = Payload.of(
                    methodFrom(exchange),
                    exchange.getIn().getHeaders(),
                    exception);

            exchange.getIn().setBody(new ExceptionEvent(
                    instantFrom(exchange),
                    correlationFrom(exchange),
                    safeMethodName(methodFrom(exchange)),
                    service._dwa.getPropertiesDefaultName(),
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
        if( operation == null){
            return null;
        }

        if(operation instanceof  String){
            return Operation.of((String)operation);
        }
        return (Operation)operation ;
    }
}
