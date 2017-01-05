package drinkwater.core.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import drinkwater.IServiceConfiguration;
import drinkwater.ITracer;
import drinkwater.ServiceScheme;
import drinkwater.ServiceState;
import drinkwater.core.CamelContextFactory;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.RouteBuilders;
import drinkwater.core.ServiceRepository;
import drinkwater.trace.Payload;
import drinkwater.trace.ServerReceivedEvent;
import drinkwater.trace.ServerSentEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.UUID;
import java.util.logging.Logger;

import static drinkwater.DrinkWaterConstants.*;

/**
 * Created by A406775 on 29/12/2016.
 */
public class Service implements drinkwater.IDrinkWaterService {

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

    public String lookupProperty(String s) throws Exception {
        return getPropertiesComponent().parseUri(s);
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
        this.getCamelContext().addRoutes(createDirectServiceRoutes());

        if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanObject) {
            //nothing to configure here
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanClass) {
            this.camelContext.addRoutes(RouteBuilders.mapBeanClassRoutes(serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Rest) {
            this.camelContext.addRoutes(RouteBuilders.mapRestRoutes(serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Task) {
            this.camelContext.addRoutes(RouteBuilders.mapCronRoutes(this._dwa.getName(), serviceRepository, this));
        }
    }

    public RouteBuilder createDirectServiceRoutes() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(ROUTE_CheckFlowIDHeader).process(exchange -> {
                    if (exchange.getIn().getHeader(FlowCorrelationIDKey) == null) {
                        exchange.getIn().setHeader(FlowCorrelationIDKey, UUID.randomUUID().toString());
                    }
                });

                from(ROUTE_serverReceivedEvent)
                        .to(ROUTE_CheckFlowIDHeader)
                        .wireTap("direct:createServerReceivedEventAndTrace");

                from(ROUTE_serverSentEvent)
                        .to(ROUTE_CheckFlowIDHeader)
                        .wireTap("direct:createServerSentEventAndTrace");

                from("direct:createServerReceivedEventAndTrace").process(exchange -> {
                    String correlationIdFlow = (String) exchange.getIn().getHeader(FlowCorrelationIDKey);
                    ServerReceivedEvent event = new ServerReceivedEvent(correlationIdFlow, "?", Payload.of(exchange.getIn().getBody()));
                    exchange.getIn().setBody(event);
                }).to(ROUTE_trace);

                from("direct:createServerSentEventAndTrace").process(exchange -> {
                    String correlationIdFlow = (String) exchange.getIn().getHeader(FlowCorrelationIDKey);
                    ServerSentEvent event = new ServerSentEvent(correlationIdFlow, "?", Payload.of(exchange.getIn().getBody()));
                    exchange.getIn().setBody(event);
                }).to(ROUTE_trace);
            }
        };
    }


    @Override
    public ServiceState getState() {
        return state;
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
