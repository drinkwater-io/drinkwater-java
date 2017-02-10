package test.drinkwater.datasource.configurations;

import drinkwater.ApplicationBuilder;
import drinkwater.datastore.JndiSqlDataStore;

public class JndiDataStoreConfiguration extends ApplicationBuilder {

    @Override
    public void configure() {
        addStore("test", JndiSqlDataStore.class);
    }
}