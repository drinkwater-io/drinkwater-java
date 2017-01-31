package test.drinkwater.datasource.postgres.embedded;

import drinkwater.ApplicationBuilder;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.datasource.postgres.embedded.EmbeddedPostgresDataStore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.junit.Assert.*;

/**
 * Created by A406775 on 11/01/2017.
 */
public class TestMigration {


    @Test
    public void shouldApplyMigrations() throws Exception {

        try (DrinkWaterApplication app = DrinkWaterApplication.create(options().use(TestMigrationConfiguration.class).autoStart())) {

            EmbeddedPostgresDataStore store = app.getStore("test");
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

class TestMigrationConfiguration extends ApplicationBuilder {

    public TestMigrationConfiguration(){}
    @Override
    public void configure() {
        addStore("test", EmbeddedPostgresDataStore.class);
    }
}