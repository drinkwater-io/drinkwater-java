package drinkwater.datasource.postgres.embedded;


import drinkwater.datasource.SqlDataStore;

import java.io.IOException;

/**
 * Created by A406775 on 11/01/2017.
 */
public class PostgresDataStore extends SqlDataStore {

    private static String DRIVER_CLASS_NAME = "org.postgresql.Driver";

//    public PostgresDataStore(String storeName, String dbUrl, String user, String password, String schema, String[] schemaLocation){
//        this(storeName, dbUrl, user, password, user, password, schema, schemaLocation);
//    }
//
//
//    public PostgresDataStore(String storeName, String dbUrl, String adminUser, String adminpassword,
//                             String user, String password, String schema, String[] schemaLocation){
//        super(storeName,
//                getPostgresConfig(dbUrl,adminUser, adminpassword, schema),
//                getPostgresConfig(dbUrl,user, password, schema),
//                schemaLocation);
//    }
//
//    private static DatasourceConfiguration getPostgresConfig(String url, String user, String password, String schema){
//        return new DatasourceConfiguration(url, DRIVER_CLASS_NAME , user, password, schema);
//    }

    protected String getJdbcDriver(){
        return DRIVER_CLASS_NAME;
    }

    @Override
    protected void doClose() throws IOException {

    }

    @Override
    protected void doStart() throws IOException {

    }
}
