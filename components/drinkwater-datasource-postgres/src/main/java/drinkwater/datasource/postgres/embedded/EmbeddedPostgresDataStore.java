package drinkwater.datasource.postgres.embedded;


import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import drinkwater.DatasourceConfiguration;
import drinkwater.datasource.SqlDataStore;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;

/**
 * Created by A406775 on 11/01/2017.
 */
public class EmbeddedPostgresDataStore extends SqlDataStore {

    public EmbeddedPostgres pg;

    public String user;

    public String dbName;

    public String password;

    public String schema;

    public EmbeddedPostgresDataStore(
            String userName, String password, String dbName, String schema, String[] schema_Location) {
        super("embedded-postgres-server", schema_Location);
        this.user = userName;
        this.dbName = dbName;
        this.password = password;
        this.schema = schema;
    }

    @Override
    protected void doClose() throws IOException {
        pg.close();
    }

    @Override
    protected void doStart() throws IOException {
        pg = EmbeddedPostgres.start();
        //DataSource ds = this.getDatabase(user, dbName, pg.getPort(), password);

        PGSimpleDataSource ds = (PGSimpleDataSource)pg.getPostgresDatabase();
        setDataSource(ds);

        setConfiguration(new DatasourceConfiguration(ds.getUrl(),
                "org.postgresql.Driver",
                ds.getUser(), ds.getPassword(), schema));

    }



//    //currently opentable does not support password this may change in future release
//    public DataSource getDatabase(String userName, String dbName, int port,String password)
//    {
//        final PGSimpleDataSource ds = new PGSimpleDataSource();
//        ds.setServerName("localhost");
//        ds.setPortNumber(port);
//        ds.setDatabaseName(dbName);
//        ds.setUser(userName);
//
//        Map<String, String> props = new HashMap<>();
//        props.put("password",password);
//
//        props.forEach((propertyKey, propertyValue) -> {
//            try {
//                ds.setProperty(propertyKey, propertyValue);
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        return ds;
//    }
}
