package test.drinkwater.core.model.forStore;

import drinkwater.IDataStore;

public class SimpleDataStoreDependentService implements ISimpleDataStoreDependentService{

    public IDataStore dataStore;

    @Override
    public String getStoreInfo() {
        return dataStore.getClass().getName();
    }
}
