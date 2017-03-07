package drinkwater.feature.trace;

import drinkwater.ComponentBuilder;
import drinkwater.Feature;
import drinkwater.FeatureBuilder;
import drinkwater.IBaseEventLogger;
import drinkwater.trace.Operation;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;

import static drinkwater.DrinkWaterConstants.ApplicationName;
import static drinkwater.DrinkWaterConstants.BeanOperationName;
import static drinkwater.DrinkWaterConstants.ComponentName;
import static drinkwater.feature.trace.TraceRouteBuilder.*;

public class TraceFeature implements Feature<TraceFeature> {

    public IBaseEventLogger logger;

    public IBaseEventLogger getLogger() {
        return logger;
    }

    public void setLogger(IBaseEventLogger logger) {
        this.logger = logger;
    }

    @Override
    public void afterServiceExposed(RouteDefinition pd, Method method, ComponentBuilder componentBuilder) {
        String appName = componentBuilder.getBuilder().getConfiguration().getApplicationName();
        String componentName = componentBuilder.getBuilder().getName();
        pd
                .setHeader(BeanOperationName)
                .constant(Operation.of(method))
                .setHeader(ApplicationName)
                .constant(appName)
                .setHeader(ComponentName)
                .constant(componentName)
                .to(serverReceivedEventRouteFor(componentName));
    }

    @Override
    public void afterServiceTargeted(RouteDefinition pd, Method method, ComponentBuilder componentBuilder) {
        String appName = componentBuilder.getBuilder().getConfiguration().getApplicationName();
        String componentName = componentBuilder.getBuilder().getName();
        pd
                .setHeader(BeanOperationName)
                .constant(Operation.of(method))
                .setHeader(ApplicationName)
                .constant(appName)
                .setHeader(ComponentName)
                .constant(componentName)
                .to(serverSentEventRouteFor(componentName));
    }

    @Override
    public void configureContext(CamelContext context, ComponentBuilder componentBuilder) throws Exception {
        context.addRoutes(createTracingRoute(componentBuilder.getBuilder().getName()));
            context.addRoutes(new TraceRouteBuilder(componentBuilder.getBuilder().getName()));
    }

    private RouteBuilder createTracingRoute(String componentName) {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {

                from(traceRouteFor(componentName)).bean(getLogger(), "logEvent(${body})");

            }
        };
    }



}
