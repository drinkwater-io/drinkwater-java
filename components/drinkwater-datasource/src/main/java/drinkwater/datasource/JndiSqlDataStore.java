package drinkwater.datasource;

import drinkwater.IDataStore;
import org.flywaydb.core.Flyway;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JndiSqlDataStore implements IDataStore {

    public String migrationJndiLookupName;

    public String jndiLookupName;

    public String schemaLocation;

    private javax.sql.DataSource dataSource;
    private javax.sql.DataSource migrationDataSource;

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
    public void start() throws Exception {

    }

    @Override
    public void configure() throws Exception {
        Context jndiContext = new InitialContext();
        if(migrationJndiLookupName == null){
            migrationJndiLookupName = jndiLookupName;
        }
        migrationDataSource = (DataSource)jndiContext.lookup(migrationJndiLookupName);
        dataSource = (DataSource)jndiContext.lookup(jndiLookupName);
    }

    @Override
    public void close() throws IOException {

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
}
