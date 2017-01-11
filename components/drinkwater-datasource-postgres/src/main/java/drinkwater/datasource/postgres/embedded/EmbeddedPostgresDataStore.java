package drinkwater.datasource.postgres.embedded;


import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import drinkwater.datasource.SqlDataStore;

import java.io.IOException;

/**
 * Created by A406775 on 11/01/2017.
 */
public class EmbeddedPostgresDataStore extends SqlDataStore {

    public EmbeddedPostgres pg;

    public EmbeddedPostgresDataStore(String... schema_Location) {
        super("embedded-postgres-server");
        try {
            //fixme : add autostart config
            start();
        } catch (Exception e) {

            throw new RuntimeException("could not start embedded server", e);
        }
    }

    @Override
    protected void doClose() throws IOException {
        pg.close();
    }

    @Override
    protected void doStart() throws IOException {
        pg = EmbeddedPostgres.start();
        setDataSource(pg.getPostgresDatabase());

    }
}
