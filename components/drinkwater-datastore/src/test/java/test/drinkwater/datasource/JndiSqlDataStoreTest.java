package test.drinkwater.datasource;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.datastore.JndiSqlDataStore;
import drinkwater.test.jndi.MockJndiContextFactoryRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import test.drinkwater.datasource.configurations.JndiDataStoreConfiguration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.junit.Assert.*;

public class JndiSqlDataStoreTest {

    public static EmbeddedPostgres pg;

    @Rule
    public MockJndiContextFactoryRule jndiFactory = new MockJndiContextFactoryRule();

    @BeforeClass
    public static void setUp(){
        try {
            pg = EmbeddedPostgres.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void close(){
        try {
            pg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldApplyMigrations() throws Exception {

        //bind the datasources
        DataSource migrationdataSource = pg.getPostgresDatabase();
        DataSource userdataSource = pg.getPostgresDatabase();

        //bind to context
        Context ctx = new InitialContext();
        ctx.bind("java:test/datasources/migrationDS", migrationdataSource);
        ctx.bind("java:test/datasources/testDS", userdataSource);

        try (DrinkWaterApplication app = DrinkWaterApplication.create("jndi-datastore-test",
                options().use(JndiDataStoreConfiguration.class).autoStart())) {

            JndiSqlDataStore store = app.getStore("test");
            store.executeNoQuery("INSERT INTO contact(id, first_name, last_name) VALUES (2 , 'Jean-Marc', 'Canon');");

            try (Connection c = store.getConnection()) {
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT * from contact");
                assertTrue(rs.next());
                assertEquals(2, rs.getInt(1));
                assertFalse(rs.next());
            }
        }
    }
}
