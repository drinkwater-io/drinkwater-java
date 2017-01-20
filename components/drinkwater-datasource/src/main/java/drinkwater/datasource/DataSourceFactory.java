package drinkwater.datasource;

import drinkwater.DatasourceConfiguration;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
//import org.apache.tomcat.jdbc.pool.DataSource;
//import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * Created by A406775 on 11/01/2017.
 */
public class DataSourceFactory {

    public static DataSource createDataSource(DatasourceConfiguration configuration){

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




//        PoolProperties p = new PoolProperties();
////        Properties props = new Properties();
////        props.setProperty("user","user");
////        props.setProperty("password",configuration.getPassword());
////
////        p.setDbProperties(props);
//        p.setUrl(configuration.getUrl());
//        p.setDefaultAutoCommit(true);
//        p.setDriverClassName(configuration.getDriverClassname());
//        p.setUsername(configuration.getUser......not password, was fault());
//        p.setPassword(configuration.getPassword());
//        p.setJmxEnabled(true);
//        p.setTestWhileIdle(true);
//        p.setTestOnBorrow(true);
//        p.setValidationQuery("SELECT 1");
//        p.setTestOnReturn(true);
//        p.setValidationInterval(30000);
//        p.setTimeBetweenEvictionRunsMillis(30000);
//        p.setMaxActive(100);
//        p.setInitialSize(10);
//        p.setMaxWait(10000);
//        p.setRemoveAbandonedTimeout(60);
//        p.setMinEvictableIdleTimeMillis(30000);
//        p.setMinIdle(100);
//        p.setLogAbandoned(true);
//        p.setRemoveAbandoned(true);
////        p.setJdbcInterceptors(
////                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
////                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
//        DataSource datasource = new DataSource();
//        datasource.setPoolProperties(p);
//
//        return datasource;
    }

//    BasicDataSource dbcpDataSource = new BasicDataSource();
//        dbcpDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dbcpDataSource.setUrl("jdbc:mysql://localhost:3306/database");
//        dbcpDataSource.setUsername("user");
//        dbcpDataSource.setPassword("************");
//
//    // Enable statement caching (Optional)
//        dbcpDataSource.setPoolPreparedStatements(true);
//        dbcpDataSource.setMaxOpenPreparedStatements(50);
}
