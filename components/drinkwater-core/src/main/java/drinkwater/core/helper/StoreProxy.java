package drinkwater.core.helper;

import drinkwater.IDataStore;
import drinkwater.IDataStore2;
import drinkwater.IDataStoreConfiguration;
import drinkwater.core.DrinkWaterApplication;

import java.io.IOException;

public class StoreProxy implements IDataStore2 {

    DrinkWaterApplication application;

    IDataStoreConfiguration configuration;

    IDataStore2 realStore;

    public StoreProxy(DrinkWaterApplication application, IDataStoreConfiguration configuration) {
        this.application = application;
        this.configuration = configuration;

        this.realStore = (IDataStore2)BeanFactory.createBean(application, configuration);

    }

    @Override
    public void migrate() {
        realStore.migrate();
    }

    @Override
    public void start() throws Exception {
        realStore.start();
    }

    @Override
    public void close() throws IOException {
        realStore.close();
    }

    @Override
    public void configure() throws Exception {
        realStore.configure();
    }

    public IDataStore getTarget(){
        return realStore;
    }
}
