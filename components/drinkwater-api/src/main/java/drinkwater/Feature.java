package drinkwater;

import org.apache.camel.CamelContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;

public interface Feature<B extends Feature> {
    default void afterServiceExposed(RouteDefinition routeDefinition, Method method, ComponentBuilder componentBuilder){}

    default void afterServiceTargeted(RouteDefinition routeDefinition, Method method, ComponentBuilder componentBuilder){}

    default void configureContext(CamelContext context, ComponentBuilder componentBuilder) throws Exception {}
}
