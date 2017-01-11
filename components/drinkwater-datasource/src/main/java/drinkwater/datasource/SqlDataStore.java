package drinkwater.datasource;

import drinkwater.DatasourceConfiguration;
import drinkwater.IDataStore;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by A406775 on 11/01/2017.
 */
public abstract class SqlDataStore implements IDataStore {

    private final String name;

    private javax.sql.DataSource dataSource;

    private String[] schemaLocations;

    public SqlDataStore(String name) {
        this.name = name;
    }

    public SqlDataStore(String name, DatasourceConfiguration dataSource, String... schemaLocation) {
        this.name = name;
        this.dataSource = DataSourceFactory.createDataSource(dataSource);
        this.schemaLocations = schemaLocation;
    }

    @Override
    public void migrate() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations(schemaLocations);
        flyway.migrate();
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

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected abstract void doClose() throws IOException;

    protected abstract void doStart() throws IOException;
}
