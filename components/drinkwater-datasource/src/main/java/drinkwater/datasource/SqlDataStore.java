package drinkwater.datasource;

import drinkwater.DatasourceConfiguration;
import drinkwater.IDataStore;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//import org.dbunit.dataset.IDataSet;
//import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
//import org.dbunit.operation.DatabaseOperation;

/**
 * Created by A406775 on 11/01/2017.
 */
public abstract class SqlDataStore implements IDataStore {

    private final String name;

    private DatasourceConfiguration migrationConfiguration;

    private DatasourceConfiguration configuration;

    private javax.sql.DataSource dataSource;

    private javax.sql.DataSource migrationDataSource;

    private String[] schemaLocations;

    public SqlDataStore(String name,
                        DatasourceConfiguration migrationConfiguration,
                        DatasourceConfiguration configuration,
                        String... schemaLocation) {
        this.name = name;
        this.migrationConfiguration = migrationConfiguration;
        this.configuration = configuration;

        this.schemaLocations = schemaLocation;
    }

    public SqlDataStore(String name, Properties prop, String... schemaLocation) {

        String user = prop.getProperty("datastore.user");
        String password = prop.getProperty("datastore.password");
        String schema = prop.getProperty("datastore.schema");
        String url = prop.getProperty("datastore.url");

        String migrationUser = prop.getProperty("migration.datastore.user");
        String migrationPassword = prop.getProperty("migration.datastore.password");
        String migrationSchema = prop.getProperty("migration.datastore.schema");
        String migrationUrl = prop.getProperty("migration.datastore.url");


        DatasourceConfiguration config = new DatasourceConfiguration(
                url, "org.postgresql.Driver",
                user, password, schema);

        DatasourceConfiguration migrationConfig = new DatasourceConfiguration(
                migrationUrl, "org.postgresql.Driver",
                migrationUser, migrationPassword, migrationSchema);

        this.name = name;
        this.migrationConfiguration = migrationConfig;
        this.configuration = config;
        this.schemaLocations = schemaLocation;
    }


    @Override
    public void migrate() {
        if (schemaLocations != null) {
            Flyway flyway = new Flyway();
            flyway.setLocations(schemaLocations);
            flyway.setDataSource(migrationDataSource);
            flyway.migrate();
        }
    }

//    public void cleanAndInject(String resourceFilePath) throws Exception {
//        IDataSet ds = readDataSet(resourceFilePath);
//        cleanlyInsert(ds);
//    }
//
//    private void cleanlyInsert(IDataSet dataSet) throws Exception {
//        //FIXME this assumes we always use postgres
//        PostgresDataSourceTester databaseTester = new PostgresDataSourceTester(this.getDataSource());
//        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
//        databaseTester.setDataSet(dataSet);
//        databaseTester.onSetup();
//    }

//    private IDataSet readDataSet(String fileName) throws Exception {
//        return new FlatXmlDataSetBuilder().build(getFullFilepath(fileName));
//    }

    private File getFullFilepath(String resourceFilePath) throws IOException {

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(resourceFilePath).getFile());

        return file;
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

//    public void setDataSource(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    protected abstract void doClose() throws IOException;

    protected abstract void doStart() throws IOException;

    protected DatasourceConfiguration getMigrationConfiguration() {
        return migrationConfiguration;
    }

    protected DatasourceConfiguration getConfiguration() {
        return configuration;
    }

    //    public DatasourceConfiguration getConfiguration() {
//        return configuration;
//    }
//
//    public void setConfiguration(DatasourceConfiguration configuration) {
//        this.configuration = configuration;
//    }
//
//    public void resetConfiguration(DatasourceConfiguration configuration) {
//        this.dataSource = DataSourceFactory.createDataSource(configuration);
//
//        this.configuration = configuration;
//    }
}
