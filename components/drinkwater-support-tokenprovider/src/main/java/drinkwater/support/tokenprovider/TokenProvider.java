package drinkwater.support.tokenprovider;

import drinkwater.ApplicationBuilder;
import drinkwater.bean.BeanComponent;
import drinkwater.rest.RestComponent;
import drinkwater.security.IAuthenticationService;
import drinkwater.security.ITokenProvider;

public class TokenProvider {
    public static void configure(ApplicationBuilder builder, Class<? extends IAuthenticationService> service){

        builder.expose(service).as(BeanComponent.class).named(service.getClass().getSimpleName());

        builder.expose(SimpleTokenProvider.class)
                .as(RestComponent.class)
                .named("auth");
    }

    public static <B extends IAuthenticationService> void configure(ApplicationBuilder builder, B service){

        builder.expose(service).as(BeanComponent.class).named(service.getClass().getSimpleName());

        builder.expose(SimpleTokenProvider.class)
                .as(RestComponent.class)
                .named("auth");
    }
}
