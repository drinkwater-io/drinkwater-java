package drinkwater;

import org.apache.camel.CamelContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;

public interface Feature {
    default void afterServiceExposed(RouteDefinition routeDefinition, Method method){}

    default void afterServiceTargeted(RouteDefinition routeDefinition, Method method){}

    default void configureContext(CamelContext context) throws Exception {}
}
