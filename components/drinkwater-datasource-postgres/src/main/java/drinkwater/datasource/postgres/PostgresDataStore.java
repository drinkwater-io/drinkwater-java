package drinkwater.datasource.postgres;


import drinkwater.datasource.SqlDataStore;

import java.io.IOException;

/**
 * Created by A406775 on 11/01/2017.
 */
public class PostgresDataStore extends SqlDataStore {

    private static String DRIVER_CLASS_NAME = "org.postgresql.Driver";

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
