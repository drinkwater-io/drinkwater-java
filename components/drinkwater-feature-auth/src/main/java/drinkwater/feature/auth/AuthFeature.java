package drinkwater.feature.auth;

import drinkwater.ComponentBuilder;
import drinkwater.DrinkWaterPropertyConstants;
import drinkwater.Feature;
import drinkwater.FeatureBuilder;
import drinkwater.helper.CamelContextHelper;
import drinkwater.security.UnauthorizedException;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;

import static drinkwater.DrinkWaterPropertyConstants.Authentication_Token_Encryption_Key;
import static drinkwater.DrinkWaterPropertyConstants.Authentication_Token_Provider;
import static org.apache.camel.builder.Builder.constant;

public class AuthFeature implements Feature, FeatureBuilder<AuthFeature> {

    @Override
    public void afterServiceExposed(RouteDefinition routeDefinition, Method method, ComponentBuilder componentBuilder) {

        routeDefinition.process(new SecurityProcessor());

        routeDefinition.onException(UnauthorizedException.class)
                .handled(true)
                .setHeader("WWW-Authenticate").constant("TOKEN")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(401))
                .setBody().constant("Unauthorized");

    }

    @Override
    public void configureContext(CamelContext context, ComponentBuilder componentBuilder) throws Exception {
        String secret = (String)componentBuilder.getBuilder()
                .lookupProperty(String.class, Authentication_Token_Encryption_Key);

        SimpleTokenValidation tokenProvider = new SimpleTokenValidation(secret);

        CamelContextHelper.registerBean(context,
                Authentication_Token_Provider,
                tokenProvider);
    }

    @Override
    public AuthFeature getFeature() {
        return new AuthFeature();
    }
}
