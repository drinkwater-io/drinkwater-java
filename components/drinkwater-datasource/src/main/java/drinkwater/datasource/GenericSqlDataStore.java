package drinkwater.datasource;

import drinkwater.IDataStore;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GenericSqlDataStore implements IDataStore {
    public String schemaLocation;

    protected javax.sql.DataSource dataSource;
    protected javax.sql.DataSource migrationDataSource;

    @Override
    public void migrate() {
        if (schemaLocation != null) {
            Flyway flyway = new Flyway();
            flyway.setLocations(schemaLocation);
            flyway.setDataSource(migrationDataSource);
            flyway.migrate();
        }
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

    public void executeInsert(String tableName, Map<String, Object> columnValues) {

        String columnArguments = columnValues.keySet().stream()
                .collect(Collectors.joining(","));
        String valuesArguments = columnValues.keySet().stream().map(k -> "?")
                .collect(Collectors.joining(","));
        String statement = String.format("Insert Into %s (%s) VALUES (%s)", tableName, columnArguments, valuesArguments);

        try (Connection connection = getConnection()) {
            PreparedStatement insertStatement = connection.prepareStatement(statement);
            int index = 1;
            for (Object value : columnValues.values()) {
                if(value instanceof Instant){
                    insertStatement.setObject(index++, LocalDateTime.ofInstant(((Instant)value), ZoneOffset.systemDefault()));
                }else {
                    insertStatement.setObject(index++, value);
                }
            }
            insertStatement.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
