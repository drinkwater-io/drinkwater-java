package test.drinkwater.core.model.forStore;

import drinkwater.ServiceConfigurationBuilder;
import test.drinkwater.core.model.ITestService;
import test.drinkwater.core.model.TestServiceImpl;


public class StoreServiceConfiguration extends ServiceConfigurationBuilder {

    public StoreServiceConfiguration(){}

    @Override
    public void configure() {

        addService("test-service-in-store", ITestService.class, TestServiceImpl.class);
        addStore2("tt", SimpleTestStore.class);
    }


}
