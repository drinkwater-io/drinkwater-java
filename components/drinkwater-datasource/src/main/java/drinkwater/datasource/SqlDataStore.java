package drinkwater.datasource;

import drinkwater.DatasourceConfiguration;
import drinkwater.IDataStore;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

//import org.dbunit.dataset.IDataSet;
//import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
//import org.dbunit.operation.DatabaseOperation;

/**
 * Created by A406775 on 11/01/2017.
 */
public abstract class SqlDataStore implements IDataStore {

    public String user;
    public String password;
    public String schema;
    public String url;
    public String jdbcDriver;
    public String migrationUser;
    public String migrationPassword;
    public String migrationSchema;
    public String migrationUrl;
    public String schemaLocation;

    private DatasourceConfiguration migrationConfiguration;
    private DatasourceConfiguration configuration;
    private javax.sql.DataSource dataSource;
    private javax.sql.DataSource migrationDataSource;

    @Override
    public void configure() throws Exception {
        DatasourceConfiguration config = buildConfiguration();

        DatasourceConfiguration migrationConfig = buildMigrationConfiguration();

        //  this.name = name;
        this.migrationConfiguration = migrationConfig;
        this.configuration = config;
    }

    protected DatasourceConfiguration buildConfiguration(){
        return new DatasourceConfiguration(
                url, jdbcDriver,
                user, password, schema);
    }

    protected DatasourceConfiguration buildMigrationConfiguration(){
        String migrationUrlToUse = (this.migrationUrl == null)? url: this.migrationUrl;
        String userToUse = (this.migrationUser == null)? user: this.migrationUser;
        String passwordToUse = (this.migrationPassword == null)? password: this.migrationPassword;
        String schemaToUse = (this.migrationSchema == null)? schema: this.migrationSchema;

        return new DatasourceConfiguration(
                migrationUrlToUse, jdbcDriver,
                userToUse, passwordToUse, schemaToUse);
    }

    protected String getJdbcDriver(){
        return jdbcDriver;
    }

    @Override
    public void migrate() {
        if (schemaLocation != null) {
            Flyway flyway = new Flyway();
            flyway.setLocations(schemaLocation);
            flyway.setDataSource(migrationDataSource);
            flyway.migrate();
        }
    }

    @Override
    public final void start() throws Exception {
        doStart();
        this.migrationDataSource = DataSourceFactory.createDataSource(migrationConfiguration);
        this.dataSource = DataSourceFactory.createDataSource(configuration);
    }

    @Override
    public final void close() throws IOException {
        doClose();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void executeNoQuery(String sql) {
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected abstract void doClose() throws IOException;

    protected abstract void doStart() throws IOException;

    protected DatasourceConfiguration getMigrationConfiguration() {
        return migrationConfiguration;
    }

    protected DatasourceConfiguration getConfiguration() {
        return configuration;
    }

}
