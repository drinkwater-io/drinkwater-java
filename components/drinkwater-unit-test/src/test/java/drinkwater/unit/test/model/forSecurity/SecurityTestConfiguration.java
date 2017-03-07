package drinkwater.unit.test.model.forSecurity;

import drinkwater.ApplicationBuilder;
import drinkwater.feature.auth.AuthFeature;
import drinkwater.rest.RestComponent;
import drinkwater.support.tokenprovider.TokenProvider;
import drinkwater.unit.test.model.TestServiceImpl;

/**
 * Created by A406775 on 4/01/2017.
 */
public class SecurityTestConfiguration extends ApplicationBuilder {
    @Override
    public void configure() {
//        TokenProvider.configure(this, );
        expose(TestServiceImpl.class).as(RestComponent.class).named("test");
        expose(TestServiceImpl.class)
                .as(RestComponent.class)
                .named("secured")
        .use(new AuthFeature());
//            addService("test", ITestService.class, new TestServiceImpl())
//                    .useTracing(true)
//                    .asRest();
//
//            addService("secured", ITestService.class, new TestServiceImpl())
//                    .useTracing(true)
//                    .asRest();

    }
}

