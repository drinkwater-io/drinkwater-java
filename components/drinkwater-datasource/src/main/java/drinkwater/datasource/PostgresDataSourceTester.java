package drinkwater.datasource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;

import javax.sql.DataSource;

/**
 * Created by A406775 on 11/01/2017.
 */
public class PostgresDataSourceTester extends DataSourceDatabaseTester {
    public PostgresDataSourceTester(DataSource dataSource) {
        super(dataSource);
    }

    public PostgresDataSourceTester(DataSource dataSource, String schema) {
        super(dataSource, schema);
    }

    @Override
    public IDatabaseConnection getConnection() throws Exception {
        IDatabaseConnection conn = super.getConnection();
        DWPostgresqlDataTypeFactory fact = new DWPostgresqlDataTypeFactory();
        conn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, fact);

        return conn;
    }
}
