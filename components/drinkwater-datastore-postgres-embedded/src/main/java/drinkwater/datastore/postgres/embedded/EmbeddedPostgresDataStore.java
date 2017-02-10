package drinkwater.datastore.postgres.embedded;


import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import drinkwater.DatasourceConfiguration;
import drinkwater.datastore.AbstractSqlDataStore;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by A406775 on 11/01/2017.
 */
public class EmbeddedPostgresDataStore extends AbstractSqlDataStore {

    public EmbeddedPostgres pg;

    public EmbeddedPostgresDataStore(){
        this.migrationUser = "postgres";
        this.migrationPassword = "";
        this.schema = "public";
    }

    private static DatasourceConfiguration getPostgresConfigWithoutUrl(String user, String password, String schema){
        return new DatasourceConfiguration(null, "org.postgresql.Driver", user, password, schema);
    }

    protected DatasourceConfiguration buildConfiguration(){
        String userToUse = (this.user == null)? migrationUser: this.user;
        String passwordToUse = (this.password == null)? migrationPassword: this.password;
        return getPostgresConfigWithoutUrl(userToUse, passwordToUse, schema);
    }

    protected DatasourceConfiguration buildMigrationConfiguration(){
        return getPostgresConfigWithoutUrl(migrationUser, migrationPassword, schema);

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

    @Override
    protected DataSource provideMigrationDataSource(DatasourceConfiguration configuration) {
        return pg.getPostgresDatabase();
    }

    @Override
    protected DataSource provideDataSource(DatasourceConfiguration configuration) {
        return pg.getDatabase(configuration.getUser(), "postgres");
    }

}
