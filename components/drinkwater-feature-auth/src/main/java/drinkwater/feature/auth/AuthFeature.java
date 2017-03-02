package drinkwater.feature.auth;

import drinkwater.DrinkWaterPropertyConstants;
import drinkwater.Feature;
import drinkwater.security.UnauthorizedException;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;

import static org.apache.camel.builder.Builder.constant;

public class AuthFeature implements Feature {

    @Override
    public void afterServiceExposed(RouteDefinition routeDefinition, Method method) {

        routeDefinition.process(new SecurityProcessor());

        routeDefinition.onException(UnauthorizedException.class)
                .handled(true)
                .setHeader("WWW-Authenticate").constant("TOKEN")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(401))
                .setBody().constant("Unauthorized");

    }

    @Override
    public void afterServiceTargeted(RouteDefinition pd, Method method) {

    }

    @Override
    public void configureContext(CamelContext context) throws Exception {

    }
}
