package drinkwater.datasource.postgres.embedded;


import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import drinkwater.DatasourceConfiguration;
import drinkwater.datasource.SqlDataStore;

import java.io.IOException;

/**
 * Created by A406775 on 11/01/2017.
 */
public class EmbeddedPostgresDataStore extends SqlDataStore {

    public EmbeddedPostgres pg;

//    public EmbeddedPostgresDataStore(String user, String password, String schema,
//                                       String... schemaLocation){
//        super("embedded-postgres-server",
//                getPostgresConfigWithoutUrl("postgres", "", schema),
//                getPostgresConfigWithoutUrl(user, password, schema),
//                schemaLocation);
//    }

    private static DatasourceConfiguration getPostgresConfigWithoutUrl(String user, String password, String schema){
        return new DatasourceConfiguration(null, "org.postgresql.Driver", user, password, schema);
    }

    protected DatasourceConfiguration buildConfiguration(){
        return getPostgresConfigWithoutUrl(user, password, schema);
    }

    protected DatasourceConfiguration buildMigrationConfiguration(){
        return getPostgresConfigWithoutUrl("postgres", "", schema);

    }

    @Override
    protected void doClose() throws IOException {
        pg.close();
    }

    @Override
    protected void doStart() throws IOException {
        pg = EmbeddedPostgres.start();
        String dbName = "postgres"; // this is inherent to opentable embedded postgres library The db must be called postgres

        //init the migration config
        String migrationUserName = getMigrationConfiguration().getUser();
        getMigrationConfiguration().setUrl(pg.getJdbcUrl(migrationUserName, dbName));

        //init the normal config
        String userName = getConfiguration().getUser();
        getConfiguration().setUrl(pg.getJdbcUrl(userName, dbName));
    }

}
