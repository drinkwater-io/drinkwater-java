package drinkwater.datasource;

import java.io.IOException;

public class GenericSqlDataStore extends SqlDataStore {

//    public GenericSqlDataStore(String name, Properties properties, String... schemaLocation){
//        super(name, properties, schemaLocation);
//    }
//
//    public GenericSqlDataStore(String name, DatasourceConfiguration migrationConfig, DatasourceConfiguration config) {
//        super(name, migrationConfig, config, null );
//    }

    @Override
    protected void doClose() throws IOException {
        //does nothing
    }

    @Override
    protected void doStart() throws IOException {
//does nothing
    }
}
