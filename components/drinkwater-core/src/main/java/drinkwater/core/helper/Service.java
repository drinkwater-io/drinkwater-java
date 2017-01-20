package drinkwater.core.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import drinkwater.*;
import drinkwater.core.CamelContextFactory;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.RouteBuilders;
import drinkwater.core.ServiceRepository;
import drinkwater.trace.*;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.logging.Logger;

import static drinkwater.DrinkWaterConstants.*;

/**
 * Created by A406775 on 29/12/2016.
 */
public class Service implements drinkwater.IDrinkWaterService, IPropertyResolver {

    @JsonIgnore
    DrinkWaterApplication _dwa;
    @JsonIgnore
    DefaultCamelContext camelContext;
    @JsonIgnore
    PropertiesComponent propertiesComponent;
    @JsonIgnore
    private Logger logger = Logger.getLogger(this.getClass().getName());
    @JsonIgnore
    private ITracer tracer;

    private IServiceConfiguration serviceConfiguration;

    private ServiceState state = ServiceState.NotStarted;

//    public Service(IServiceConfiguration serviceConfiguration,
//                   ITracer tracer, DrinkWaterApplication dwa) {
//
//        //this.camelContext = fromContext;
//        this(serviceConfiguration, tracer);
//        this._dwa = dwa;
//    }

    public Service(IServiceConfiguration serviceConfiguration, ITracer tracer, DrinkWaterApplication dwa) {
        this.serviceConfiguration = serviceConfiguration;
        this.camelContext = CamelContextFactory.createCamelContext(serviceConfiguration);
        this.tracer = tracer;
        this._dwa = dwa;
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

    private static String directRouteFor(Class eventClass) {
        if (eventClass.getName().equals(ClientReceivedEvent.class.getName())) {
            return ROUTE_clientReceivedEvent;
        } else if (eventClass.getName().equals(ClientSentEvent.class.getName())) {
            return ROUTE_clientSentEvent;
        } else if (eventClass.getName().equals(ServerSentEvent.class.getName())) {
            return ROUTE_serverSentEvent;
        } else if (eventClass.getName().equals(ServerReceivedEvent.class.getName())) {
            return ROUTE_serverReceivedEvent;
        }
        throw new RuntimeException("Event currently not coded");
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    @Override
    public ITracer getTracer() {
        return tracer;
    }

    public PropertiesComponent getPropertiesComponent() {
        if (propertiesComponent == null) {
            propertiesComponent = camelContext.getComponent(
                    "properties", PropertiesComponent.class);
        }
        return propertiesComponent;
    }

    @Override
    public String lookupProperty(String s) throws Exception {
        return getPropertiesComponent().parseUri(s);
    }

    @Override
    public Object lookupProperty(Class resultType, String uri)throws Exception{
            String value = lookupProperty(uri);
            return this.camelContext.getTypeConverter().convertTo(resultType, value);
    }

    @Override
    public void start() {
        try {
            this.getCamelContext().start();
            this.state = ServiceState.Up;
        } catch (Exception e) {
            throw new RuntimeException("Could not start service", e);
        }
    }

    @Override
    public void stop() {
        try {
            this.getCamelContext().stop();
            this.state = ServiceState.Stopped;
        } catch (Exception e) {
            throw new RuntimeException("could not stop service : ", e);
        }
    }

    public void configure(ServiceRepository serviceRepository) throws Exception {

        //create tracing routes
        this.getCamelContext().addRoutes(createServiceTraceRoutes(getConfiguration().getIsTraceEnabled()));

        if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanObject) {
            //nothing to configure here
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanClass) {
            this.camelContext.addRoutes(RouteBuilders.mapBeanClassRoutes(serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Rest) {
            this.camelContext.addRoutes(RouteBuilders.mapRestRoutes(serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Task) {
            this.camelContext.addRoutes(RouteBuilders.mapCronRoutes(this._dwa.getName(), serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Routeur) {
            this.camelContext.addRoutes(RouteBuilders.mapRoutingRoutes(serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.HttpProxy) {
            this.camelContext.addRoutes(RouteBuilders.mapHttpProxyRoutes(serviceRepository, this));
        }

    }

//    private RouteBuilder createRouting(){
//        return new RouteBuilder(){
//            @Override
//            public void configure() throws Exception {
//
//            }
//        };
//    }

    private RouteBuilder createServiceTraceRoutes(boolean isTracingEnabled) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                IBaseEventLogger emptyLogger = new IBaseEventLogger() {
                    @Override
                    public void logEvent(BaseEvent event) {
                        logger.finer(event.toString());
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
                            payloadFrom(exchange)));
                }).to(tracingRoute);

                from("direct:createServerSentEventAndTrace").process(exchange -> {
                    exchange.getIn().setBody(new ServerSentEvent(
                            instantFrom(exchange),
                            correlationFrom(exchange),
                            safeMethodName(methodFrom(exchange)),
                            payloadFrom(exchange)));
                }).to(tracingRoute);

                //client events
                from("direct:createClientReceivedEventAndTrace").process(exchange -> {
                    exchange.getIn().setBody(new ClientReceivedEvent(
                            instantFrom(exchange),
                            correlationFrom(exchange),
                            safeMethodName(methodFrom(exchange)),
                            payloadFrom(exchange)));
                }).to(tracingRoute);

                from("direct:createClientSentEventAndTrace").process(exchange -> {
                    exchange.getIn().setBody(new ClientSentEvent(
                            instantFrom(exchange),
                            correlationFrom(exchange),
                            safeMethodName(methodFrom(exchange)),
                            payloadFrom(exchange)));
                }).to(tracingRoute);

                //method invocation events
                from("direct:createMISEventAndTrace").process(exchange -> {

                    exchange.getIn().setBody(new MethodInvocationStartEvent(
                            instantFrom(exchange),
                            correlationFrom(exchange),
                            safeMethodName(methodFrom(exchange)),
                            payloadFrom(exchange)));
                }).to(tracingRoute);

                from("direct:createMIEEventAndTrace").process(exchange -> {
                    exchange.getIn().setBody(new MethodInvocationEndEvent(
                            instantFrom(exchange),
                            correlationFrom(exchange),
                            safeMethodName(methodFrom(exchange)),
                            payloadFrom(exchange)));
                }).to(tracingRoute);
            }
        };
    }

    @Override
    public ServiceState getState() {
        return state;
    }

    @Override
    public Boolean sendEvent(Class eventClass, Method method, Object body) {
        this.camelContext.createProducerTemplate()
                .sendBodyAndHeader(directRouteFor(eventClass), body,
                        BeanOperationName, Operation.of(method));
        return true;
    }

    @Override
    public IServiceConfiguration getConfiguration() {
        return serviceConfiguration;
    }

    @Override
    public String toString() {
        return serviceConfiguration.getServiceName() + " as " + serviceConfiguration.getScheme() +
                " [" + serviceConfiguration.getServiceClass() + "]";
    }
}
