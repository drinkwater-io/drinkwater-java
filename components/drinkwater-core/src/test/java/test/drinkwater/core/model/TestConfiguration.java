package test.drinkwater.core.model;

import drinkwater.ServiceConfigurationBuilder;

/**
 * Created by A406775 on 4/01/2017.
 */
public class TestConfiguration extends ServiceConfigurationBuilder {
    @Override
    public void configure() {
        addService("test", ITestService.class, new TestServiceImpl()).asRest();
    }
}
