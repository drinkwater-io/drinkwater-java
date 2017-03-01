package test.drinkwater.rest.model;

import drinkwater.ApplicationBuilder;

public class RestConfiguration extends ApplicationBuilder {


    @Override
    public void configure() {
        addService("serviceA", IServiceA.class, ServiceAImpl.class)
                .asRest();

    }
}

