package test.drinkwater.core.model;

import drinkwater.ApplicationBuilder;
import drinkwater.trace.MockEventLogger;

/**
 * Created by A406775 on 4/01/2017.
 */
public class TestConfiguration extends ApplicationBuilder {
    @Override
    public void configure() {

        useEventLogger(MockEventLogger.class);

        addService("test", ITestService.class, new TestServiceImpl()).asRest();
    }
}
