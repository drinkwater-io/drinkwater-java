package drinkwater.unit.test.model.forRest;


import drinkwater.ApplicationBuilder;
import drinkwater.feature.multipart.MultiPartFeature;
import drinkwater.rest.RestComponent;

public class RestConfiguration extends ApplicationBuilder {


    @Override
    public void configure() {
        expose(ServiceAImpl.class)
                .as(RestComponent.class)
                .named("serviceA")
                .use(MultiPartFeature.class);
//before issue-20
//        addService("serviceA", IServiceA.class, ServiceAImpl.class)
//                .asRest();

    }
}