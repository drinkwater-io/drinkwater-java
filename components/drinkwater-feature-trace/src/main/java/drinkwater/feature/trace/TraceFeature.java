package drinkwater.feature.trace;

import drinkwater.Feature;
import drinkwater.trace.Operation;
import org.apache.camel.CamelContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;

import static drinkwater.DrinkWaterConstants.BeanOperationName;
import static drinkwater.feature.trace.TraceRouteBuilder.ROUTE_serverReceivedEvent;
import static drinkwater.feature.trace.TraceRouteBuilder.ROUTE_serverSentEvent;

public class TraceFeature implements Feature {
    @Override
    public void afterServiceExposed(RouteDefinition pd, Method method) {
        pd
                .setHeader(BeanOperationName)
                .constant(Operation.of(method))
                .to(ROUTE_serverReceivedEvent);

    }

    @Override
    public void afterServiceTargeted(RouteDefinition pd, Method method) {
        pd
                .setHeader(BeanOperationName)
                .constant(Operation.of(method))
                .to(ROUTE_serverSentEvent);
    }

    @Override
    public void configureContext(CamelContext context) throws Exception {
            context.addRoutes(new TraceRouteBuilder("", "", true));
    }
}
