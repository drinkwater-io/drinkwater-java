package drinkwater.datasource;

import drinkwater.DatasourceConfiguration;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * Created by A406775 on 11/01/2017.
 */
public class DataSourceFactory {

    public static DataSource createDataSource(DatasourceConfiguration configuration){
        PoolProperties p = new PoolProperties();
//        Properties props = new Properties();
//        props.setProperty("user","user");
//        props.setProperty("password",configuration.getPassword());
//
//        p.setDbProperties(props);
        p.setUrl(configuration.getUrl());
        p.setDriverClassName(configuration.getDriverClassname());
        p.setUsername(configuration.getUser());
        p.setPassword(configuration.getPassword());
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(p);

        return datasource;
    }
}
