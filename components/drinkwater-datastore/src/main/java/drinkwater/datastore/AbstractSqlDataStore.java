package drinkwater.datastore;

import drinkwater.DatasourceConfiguration;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by A406775 on 11/01/2017.
 */
public abstract class AbstractSqlDataStore extends GenericSqlDataStore {

    public String user;
    public String password;
    public String schema;
    public String url;
    public String jdbcDriver;
    public String migrationUser;
    public String migrationPassword;
    public String migrationSchema;
    public String migrationUrl;

    private DatasourceConfiguration migrationConfiguration;
    private DatasourceConfiguration configuration;

    @Override
    public void configure() throws Exception {
        DatasourceConfiguration config = buildConfiguration();
        DatasourceConfiguration migrationConfig = buildMigrationConfiguration();

        this.migrationConfiguration = migrationConfig;
        this.configuration = config;
    }

    protected DatasourceConfiguration buildConfiguration() {
        return new DatasourceConfiguration(
                url, jdbcDriver,
                user, password, schema);
    }

    protected DatasourceConfiguration buildMigrationConfiguration() {
        String migrationUrlToUse = (this.migrationUrl == null) ? url : this.migrationUrl;
        String userToUse = (this.migrationUser == null) ? user : this.migrationUser;
        String passwordToUse = (this.migrationPassword == null) ? password : this.migrationPassword;
        String schemaToUse = (this.migrationSchema == null) ? schema : this.migrationSchema;

        return new DatasourceConfiguration(
                migrationUrlToUse, jdbcDriver,
                userToUse, passwordToUse, schemaToUse);
    }

    protected String getJdbcDriver() {
        return jdbcDriver;
    }

    @Override
    public void start() throws Exception {
        doStart();
        this.migrationDataSource = provideMigrationDataSource(getMigrationConfiguration());
        this.dataSource = provideDataSource(getConfiguration());
    }

    @Override
    public void close() throws IOException {
        doClose();
    }

    protected abstract void doClose() throws IOException;

    protected abstract void doStart() throws IOException;

    protected DatasourceConfiguration getMigrationConfiguration() {
        return migrationConfiguration;
    }

    protected DatasourceConfiguration getConfiguration() {
        return configuration;
    }

    protected abstract DataSource provideMigrationDataSource(DatasourceConfiguration configuration);

    protected abstract DataSource provideDataSource(DatasourceConfiguration configuration);



}
