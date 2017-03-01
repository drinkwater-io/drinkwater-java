package drinkwater.core.helper;

import drinkwater.BeanFactory;
import drinkwater.IDataStore;
import drinkwater.IDataStoreConfiguration;
import drinkwater.core.DrinkWaterApplication;

import java.io.IOException;

public class StoreProxy implements IDataStore {

    DrinkWaterApplication application;

    IDataStoreConfiguration configuration;

    IDataStore realStore;

    public StoreProxy(DrinkWaterApplication application, IDataStoreConfiguration configuration) {
        this.application = application;
        this.configuration = configuration;

        this.realStore = (IDataStore) BeanFactory.createBean(application, configuration);

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
