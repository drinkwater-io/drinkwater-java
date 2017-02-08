package test.drinkwater.servlet;

import drinkwater.ApplicationBuilder;
import drinkwater.test.samples.ISimpleTestService;
import drinkwater.test.samples.SimpleTestServiceImpl;
import drinkwater.trace.MockEventLogger;

public class TestServiceBuilderConfig extends ApplicationBuilder {

    public void configure() {
        useTracing(true);
        useEventLogger(MockEventLogger.class);

        addService("test",
                ISimpleTestService.class,
                SimpleTestServiceImpl.class).useTracing(true).asRest();
    }
}

