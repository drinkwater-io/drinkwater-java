package test.drinkwater.core.model.forStore;

import drinkwater.ApplicationBuilder;


public class StoreApplication extends ApplicationBuilder {

    public StoreApplication(){}

    @Override
    public void configure() {

        addStore("tt", SimpleTestStore.class);

        addService("store-dependent-service",
                ISimpleDataStoreDependentService.class,
                SimpleDataStoreDependentService.class);
    }
}
