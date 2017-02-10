package test.drinkwater.datasource;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.datastore.JndiSqlDataStore;
import drinkwater.test.HttpUnitTest;
import drinkwater.test.jndi.MockJndiContextFactoryRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import test.drinkwater.datasource.configurations.SqlDataStoreEventLoggerTestConfiguration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class SqlDataStoreEventLoggerTest extends HttpUnitTest {
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
    public void shouldLogEventInDatabase() throws Exception {

        //bind the datasources
        DataSource migrationdataSource = pg.getPostgresDatabase();
        DataSource userdataSource = pg.getPostgresDatabase();

        //bind to context
        Context ctx = new InitialContext();
        ctx.bind("java:test/datasources/migrationDS", migrationdataSource);
        ctx.bind("java:test/datasources/testDS", userdataSource);

        try (DrinkWaterApplication app = DrinkWaterApplication.create("datastore-event-test",
                options().use(SqlDataStoreEventLoggerTestConfiguration.class).autoStart())) {

            JndiSqlDataStore store = app.getStore("test");

            String result = httpGetString("http://localhost:8889/test/echo?message=hello").result();

            assertThat(result).isEqualTo("hello");

            app.stop();

            try (Connection c = store.getConnection()) {
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery("SELECT count(*) from trace");
                assertTrue(rs.next());
                assertEquals(2, rs.getInt(1));
                assertFalse(rs.next());
            }

        }
    }
}
