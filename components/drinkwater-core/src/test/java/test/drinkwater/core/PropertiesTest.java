package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import test.drinkwater.core.model.forProperties.PropertiesTestConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.assertEquals;

public class PropertiesTest extends HttpUnitTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldWorkWithDefaultProperties() throws Exception {

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("properties-application", false, false);
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration("test-without-properties", null));

        try {
            propertiesApp.start();

            String result = httpGetString("http://127.0.0.1:8889/test-without-properties/info").result();
            assertEquals("test info", result);

        }
        finally {
            propertiesApp.stop();
        }
    }

    @Test
    public void shouldWorkWithApplicationPropertiesFormClassPath() throws Exception {

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("PropertiesTest-application", false, false);
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration("test-with-application-properties",null));

        try {
            propertiesApp.start();

            String result = httpGetString("http://127.0.0.1:8889/test-with-application-properties/info").result();
            assertEquals("info from application properties file", result);

        }
        finally {
            propertiesApp.stop();
        }
    }

    @Test
    public void shouldWorkWithServicePropertiesFormClassPath() throws Exception {

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("PropertiesTest-application", false, false);
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration());

        try {
            propertiesApp.start();

            String result = httpGetString("http://127.0.0.1:8889/test-properties/info").result();
            assertEquals("info from service properties file", result);

        }
        finally {
            propertiesApp.stop();
        }
    }

    @Test
    public void shouldWorkWithApplicationPropertiesFormOtherLocation() throws Exception {

        //create an external propertiesfile
        File propertiesFile = folder.newFile("external.properties");

        Files.write(Paths.get(propertiesFile.getPath()),
                "info=info from external properties file".getBytes(),
                StandardOpenOption.APPEND);

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("PropertiesTest-application", false, false);
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration("test-external-properties", propertiesFile.getPath()));

        try {
            propertiesApp.start();

            String result = httpGetString("http://127.0.0.1:8889/test-external-properties/info").result();
            assertEquals("info from external properties file", result);

        }
        finally {
            propertiesApp.stop();
        }
    }
}
