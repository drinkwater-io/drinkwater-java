package test.drinkwater.core.model.forRest;

import drinkwater.ServiceConfigurationBuilder;

public class RestConfiguration extends ServiceConfigurationBuilder {


    @Override
    public void configure() {
        addService("serviceA", IServiceA.class, ServiceAImpl.class)
                .asRest();

    }
}

