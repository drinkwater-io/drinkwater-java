package test.drinkwater.datasource.configurations;

import drinkwater.ApplicationBuilder;
import drinkwater.datastore.JndiSqlDataStore;
import drinkwater.datastore.SqlDataStoreEventLogger;
import drinkwater.test.samples.ISimpleTestService;
import drinkwater.test.samples.SimpleTestServiceImpl;

public class SqlDataStoreEventLoggerTestConfiguration extends ApplicationBuilder {

    @Override
    public void configure() {
        useTracing(true);
        useEventLogger(SqlDataStoreEventLogger.class);
        addStore("test", JndiSqlDataStore.class);
        addService("test", ISimpleTestService.class, SimpleTestServiceImpl.class)
                .useTracing(true).asRest();
    }
}