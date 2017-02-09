package drinkwater.datasource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;

public class JndiSqlDataStore extends GenericSqlDataStore {

    public String migrationJndiLookupName;

    public String jndiLookupName;


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
    public void start() throws Exception {

    }

    @Override
    public void close() throws IOException {

    }



}
