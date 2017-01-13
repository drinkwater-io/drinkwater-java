package drinkwater.datasource;

import drinkwater.DatasourceConfiguration;
import drinkwater.IDataStore;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by A406775 on 11/01/2017.
 */
public abstract class SqlDataStore implements IDataStore {

    private final String name;

    private DatasourceConfiguration configuration;

    private javax.sql.DataSource dataSource;

    private String[] schemaLocations;

    public SqlDataStore(String name, String... schemaLocation) {
        this.name = name;
        this.schemaLocations = schemaLocation;
    }

    public SqlDataStore(String name, DatasourceConfiguration dataSource, String... schemaLocation) {
        this.name = name;
        this.dataSource = DataSourceFactory.createDataSource(dataSource);
        this.schemaLocations = schemaLocation;
    }

    @Override
    public void migrate() {
        Flyway flyway = new Flyway();
        if(schemaLocations != null) {
            flyway.setLocations(schemaLocations);
        }
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

    public void cleanAndInject(String resourceFilePath) throws Exception {
        IDataSet ds = readDataSet(resourceFilePath);
        cleanlyInsert(ds);
    }

    private void cleanlyInsert(IDataSet dataSet) throws Exception {
        PostgresDataSourceTester databaseTester = new PostgresDataSourceTester(this.getDataSource());
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        //FIXME this assumes we always use postgres

        databaseTester.onSetup();
    }

    private IDataSet readDataSet(String fileName) throws Exception {
        return new FlatXmlDataSetBuilder().build(getFullFilepath(fileName));
    }

    private File getFullFilepath(String resourceFilePath) throws IOException {

        ClassLoader classLoader = this.getClass().getClassLoader();

        File file = new File(classLoader.getResource(resourceFilePath).getFile());

        return file;
    }

    @Override
    public final void start() throws Exception {
        doStart();
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

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected abstract void doClose() throws IOException;

    protected abstract void doStart() throws IOException;

    public DatasourceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DatasourceConfiguration configuration) {
        this.configuration = configuration;


    }

    public void resetConfiguration(DatasourceConfiguration configuration){
        this.dataSource = DataSourceFactory.createDataSource(configuration);

        this.configuration = configuration;
    }
}
