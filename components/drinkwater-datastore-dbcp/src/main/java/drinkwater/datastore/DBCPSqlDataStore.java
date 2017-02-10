package drinkwater.datastore;

import drinkwater.DatasourceConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;

public class DBCPSqlDataStore extends AbstractSqlDataStore {


    @Override
    protected DataSource provideMigrationDataSource(DatasourceConfiguration configuration) {

        BasicDataSource dbcpDataSource = getBasicDataSource(configuration);

        return dbcpDataSource;
    }

    @Override
    protected DataSource provideDataSource(DatasourceConfiguration configuration) {

        BasicDataSource dbcpDataSource = getBasicDataSource(configuration);

        return dbcpDataSource;
    }

    @Override
    protected void doClose() throws IOException {

    }

    @Override
    protected void doStart() throws IOException {

    }

    private static BasicDataSource getBasicDataSource(DatasourceConfiguration configuration) {
        BasicDataSource dbcpDataSource = new BasicDataSource();
        dbcpDataSource.setDriverClassName(configuration.getDriverClassname());
        dbcpDataSource.setUrl(configuration.getUrl());
        dbcpDataSource.setUsername(configuration.getUser());
        dbcpDataSource.setPassword(configuration.getPassword());

        // Enable statement caching (Optional)
        dbcpDataSource.setPoolPreparedStatements(true);
        dbcpDataSource.setValidationQuery("Select 1 ");
        dbcpDataSource.setMaxOpenPreparedStatements(50);
        dbcpDataSource.setLifo(true);
        dbcpDataSource.setMaxTotal(10);
        dbcpDataSource.setInitialSize(2);
        return dbcpDataSource;
    }
}
