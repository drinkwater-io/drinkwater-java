package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import drinkwater.trace.EventAggregator;
import drinkwater.trace.FileEventLogger;
import drinkwater.trace.MockEventLogger;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;
import test.drinkwater.core.model.forTracing.CustomTraceClass;
import test.drinkwater.core.model.forTracing.ServiceAConfiguration;
import test.drinkwater.core.model.forTracing.ServiceBConfiguration;
import test.drinkwater.core.model.forTracing.ServiceCConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Created by A406775 on 5/01/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TracingTest extends HttpUnitTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testTracing() throws Exception {

        DrinkWaterApplication app_C = DrinkWaterApplication.create("application-C", false, false);
        app_C.addServiceBuilder(new ServiceCConfiguration(true));

        DrinkWaterApplication app_B = DrinkWaterApplication.create("application-B", false, false);
        app_B.addServiceBuilder(new ServiceBConfiguration(true));

        DrinkWaterApplication app_A = DrinkWaterApplication.create("application-A", false, true);
        app_A.addServiceBuilder(new ServiceAConfiguration(true, false));

        try {
            app_A.start();
            app_B.start();
            app_C.start();

            String result = httpGetString("http://127.0.0.1:7777/serviceA/datafroma").result();

            assertEquals("data from A - data from B - data from c /computed in D/ ", result);

            EventAggregator aggregator = app_A.getEventAggregator();

            Thread.sleep(300); //let it

            assertEquals(12, aggregator.currentSize());

        } finally {
            app_A.stop();
            app_B.stop();
            app_C.stop();
        }


    }

    @Test
    public void testTracingInFile() throws IOException, InterruptedException {

        File createdFolder = folder.newFolder("tracingFolder");

        DrinkWaterApplication app_C = DrinkWaterApplication.create("application-c", false, false);
        app_C.addServiceBuilder(new ServiceCConfiguration(false));

        DrinkWaterApplication app_B = DrinkWaterApplication.create("application-b", false, false);
        app_B.addServiceBuilder(new ServiceBConfiguration(false));

        DrinkWaterApplication app_A = DrinkWaterApplication.create("application-a", false, true);
        app_A.setEventLoggerClass(FileEventLogger.class);
        app_A.addServiceBuilder(new ServiceAConfiguration(false, true));
        app_A.addProperty("application-a.FileEventLogger.folder", createdFolder.getAbsolutePath());

        try {
            app_A.start();
            app_B.start();
            app_C.start();

            String result = httpGetString("http://127.0.0.1:7777/serviceA/datafroma").result();

            assertEquals("data from A - data from B - data from c /computed in D/ ", result);

            EventAggregator aggregator = app_A.getEventAggregator();

            Thread.sleep(100); //let it
            //check created file
            List<String> expectedLines = Files.readAllLines(Paths.get(createdFolder.listFiles()[0].toURI()));
            assertEquals(2, expectedLines.size());
            assertEquals(2, aggregator.currentSize());

        }finally {
            app_A.stop();
            app_B.stop();
            app_C.stop();
        }


    }

    @Test
    public void testTracingWithCustomLogger() throws Exception {

        DrinkWaterApplication app_C = DrinkWaterApplication.create("application-C-custom-logger", false, true);
        app_C.addServiceBuilder(new ServiceCConfiguration(true));
        app_C.setEventLoggerClass(CustomTraceClass.class);
        CustomTraceClass.called = 0;

        app_C.start();

        try {
            String result = httpGetString("http://127.0.0.1:9999/serviceC/DataFromC").result();

            assertEquals("data from c", result);

            Thread.sleep(50);

            assertEquals(CustomTraceClass.called, 2);
        } finally {

            app_C.stop();
        }


    }

    @Test
    public void testTracingWithException() throws Exception {

        DrinkWaterApplication app_C = DrinkWaterApplication.create("application-withException", false, true);
        app_C.addServiceBuilder(new ServiceCConfiguration(true));
        app_C.setEventLoggerClass(MockEventLogger.class);

        app_C.start();

        try {
            String result = httpGetString("http://127.0.0.1:9999/serviceC/TestThrowException").result();

            MockEventLogger logger = (MockEventLogger) app_C.getCurrentBaseEventLogger();

            Thread.sleep(50);

            assertThat(logger.getEvents().size()).isEqualTo(2);
        } finally {

            app_C.stop();
        }

    }
}
