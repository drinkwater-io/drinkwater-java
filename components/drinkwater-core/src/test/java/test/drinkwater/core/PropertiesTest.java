package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.rest.RestService;
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

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("properties-application");
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration("test-without-properties", null));

        try {
            propertiesApp.start();

            String result = httpGetString(String.format(
                    "http://127.0.0.1:%s/test-without-properties/info",
                    propertiesApp.getServiceProperty("test-without-properties", RestService.REST_PORT_KEY))).result();
            assertEquals("test info", result);

        }
        finally {
            propertiesApp.stop();
        }
    }

    @Test
    public void shouldWorkWithApplicationPropertiesFormClassPath() throws Exception {

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("PropertiesTest-application");
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration("test-with-application-properties",null));

        try {
            propertiesApp.start();

            String result = httpGetString(String.format(
                    "http://127.0.0.1:%s/test-with-application-properties/info",
                    propertiesApp.getServiceProperty("test-with-application-properties", RestService.REST_PORT_KEY))).result();
            assertEquals("info from application properties file", result);

        }
        finally {
            propertiesApp.stop();
        }
    }

    @Test
    public void shouldWorkWithPlaceHoldersInApplicationFile() throws Exception {

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("properties-placeholder-application");
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration("ppa-service",null));

        try {
            propertiesApp.start();

            String result = httpGetString(String.format("http://127.0.0.1:%s/ppa-service/info",
                    propertiesApp.getServiceProperty("ppa-service", RestService.REST_PORT_KEY))).result();
            assertEquals("cascaded keys in application : hello john", result);

        }
        finally {
            propertiesApp.stop();
        }
    }

    @Test
    public void shouldWorkWithPlaceHoldersInServiceFile() throws Exception {

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("PropertiesTest-application");
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration("props-with-placeholders",null));

        try {
            propertiesApp.start();

            String result = httpGetString(String.format(
                    "http://127.0.0.1:%s/props-with-placeholders/info",
                    propertiesApp.getServiceProperty("props-with-placeholders", RestService.REST_PORT_KEY))).result();
            assertEquals("cascaded keys : hello cedric", result);

        }
        finally {
            propertiesApp.stop();
        }
    }

    @Test
    public void shouldWorkWithServicePropertiesFormClassPath() throws Exception {

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("PropertiesTest-application");
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration());

        try {
            propertiesApp.start();

            String result = httpGetString(String.format("http://127.0.0.1:%s/test-properties/info",
                    propertiesApp.getServiceProperty("test-properties", RestService.REST_PORT_KEY))).result();
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

        DrinkWaterApplication propertiesApp = DrinkWaterApplication.create("PropertiesTest-application");
        propertiesApp.addServiceBuilder(new PropertiesTestConfiguration("test-external-properties", propertiesFile.getPath()));

        try {
            propertiesApp.start();

            String result = httpGetString(String.format("http://127.0.0.1:%s/test-external-properties/info",
                    propertiesApp.getServiceProperty("test-external-properties", RestService.REST_PORT_KEY))).result();
            assertEquals("info from external properties file", result);

        }
        finally {
            propertiesApp.stop();
        }
    }


}
