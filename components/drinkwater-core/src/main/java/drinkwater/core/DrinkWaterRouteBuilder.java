package drinkwater.core;

import drinkwater.ComponentBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import java.lang.reflect.Method;

public class DrinkWaterRouteBuilder extends RouteBuilder {

    private ComponentBuilder componentBuilder;

    public DrinkWaterRouteBuilder(CamelContext context, ComponentBuilder componentBuilder) {
        super(context);
        this.componentBuilder = componentBuilder;
        this.componentBuilder.getBuilder().setCamelContext(context);
    }

    @Override
    public void configure() throws Exception {

        componentBuilder.getBuilder().configureRouteBuilder(this);

        Method[] methods = componentBuilder.getBuilder().getServiceClass().getDeclaredMethods();

        for (Method method :
                methods) {
            componentBuilder.getBuilder().configureMethodEndpoint(this, method);
        }
    }
}
