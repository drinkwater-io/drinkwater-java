package test.drinkwater.servlet;

import drinkwater.ServiceConfigurationBuilder;
import drinkwater.test.samples.ISimpleTestService;
import drinkwater.test.samples.SimpleTestServiceImpl;

public class TestServiceBuilderConfig extends ServiceConfigurationBuilder {

    public String applicationName;

    public void configure() {

        addService(applicationName,
                ISimpleTestService.class,
                SimpleTestServiceImpl.class).useTracing(true).asRest();
    }
}

