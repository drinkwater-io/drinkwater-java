package test.drinkwater.datasource;

import drinkwater.ApplicationBuilder;
import drinkwater.datasource.JndiSqlDataStore;

public class JndiDataStoreConfiguration extends ApplicationBuilder {

    @Override
    public void configure() {
        addStore("test", JndiSqlDataStore.class);
    }
}