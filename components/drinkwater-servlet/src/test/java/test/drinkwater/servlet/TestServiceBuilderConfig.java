package test.drinkwater.servlet;

import drinkwater.ApplicationBuilder;
import drinkwater.test.samples.ISimpleTestService;
import drinkwater.test.samples.SimpleTestServiceImpl;

public class TestServiceBuilderConfig extends ApplicationBuilder {

    public String applicationName;

    public void configure() {

        addService(applicationName,
                ISimpleTestService.class,
                SimpleTestServiceImpl.class).useTracing(true).asRest();
    }
}

