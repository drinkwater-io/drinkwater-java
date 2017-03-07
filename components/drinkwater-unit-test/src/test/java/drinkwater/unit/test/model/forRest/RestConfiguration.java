package drinkwater.unit.test.model.forRest;


import drinkwater.ApplicationBuilder;
import drinkwater.feature.multipart.MultiPartFeature;
import drinkwater.rest.RestComponent;

import static drinkwater.feature.multipart.MultiPartFeature.multipart;

public class RestConfiguration extends ApplicationBuilder {


    @Override
    public void configure() {
        expose(ServiceAImpl.class)
                .as(RestComponent.class)
                .named("serviceA")
                .use(multipart());
//before issue-20
//        addService("serviceA", IServiceA.class, ServiceAImpl.class)
//                .asRest();

    }
}