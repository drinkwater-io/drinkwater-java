package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import drinkwater.trace.*;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;
import test.drinkwater.core.model.TestConfiguration;
import test.drinkwater.core.model.forTracing.ApplicationA;
import test.drinkwater.core.model.forTracing.ApplicationB;
import test.drinkwater.core.model.forTracing.ApplicationC;
import test.drinkwater.core.model.forTracing.CustomTraceClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static drinkwater.ApplicationOptionsBuilder.options;
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

        DrinkWaterApplication app_C = DrinkWaterApplication.create("application-C");
        app_C.addServiceBuilder(new ApplicationC(false,true, null));

        DrinkWaterApplication app_B = DrinkWaterApplication.create("application-B");
        app_B.addServiceBuilder(new ApplicationB(false,true));

        DrinkWaterApplication app_A = DrinkWaterApplication.create("application-A");
        app_A.addServiceBuilder(new ApplicationA(true, true, false, MockEventLogger.class));

        try {
            app_A.start();
            app_B.start();
            app_C.start();

            String result = httpGetString("http://127.0.0.1:7777/serviceA/datafroma").result();

            assertEquals("data from A - data from B - data from c /computed in D/ ", result);

            app_A.stop();
            app_B.stop();
            app_C.stop();

            MockEventLogger aggregator = (MockEventLogger) app_A.getCurrentBaseEventLogger();

            assertThat(aggregator.count()).isEqualTo(12);
            assertThat(aggregator.getEventsOfType(ServerReceivedEvent.class).size()).isEqualTo(3);
            assertThat(aggregator.getEventsOfType(ServerSentEvent.class).size()).isEqualTo(3);
            assertThat(aggregator.getEventsOfType(ClientSentEvent.class).size()).isEqualTo(2);
            assertThat(aggregator.getEventsOfType(ClientReceivedEvent.class).size()).isEqualTo(2);
            assertThat(aggregator.getEventsOfType(MethodInvocationEndEvent.class).size()).isEqualTo(1);
            assertThat(aggregator.getEventsOfType(MethodInvocationStartEvent.class).size()).isEqualTo(1);
            assertThat(aggregator.getEventsOfType(ExceptionEvent.class).size()).isEqualTo(0);

        } finally {
            app_A.stop();
            app_B.stop();
            app_C.stop();
        }


    }

    @Test
    public void testTracingInFile() throws IOException, InterruptedException {

        File createdFolder = folder.newFolder("tracingFolder");

        DrinkWaterApplication app_C = DrinkWaterApplication.create("application-c");
        app_C.addServiceBuilder(new ApplicationC(false,false, null));

        DrinkWaterApplication app_B = DrinkWaterApplication.create("application-b");
        app_B.addServiceBuilder(new ApplicationB(false,false));

        DrinkWaterApplication app_A = DrinkWaterApplication.create("application-a");
        app_A.addServiceBuilder(new ApplicationA(true,true, true, FileEventLogger.class));
        app_A.addProperty("eventLogger.folder", createdFolder.getAbsolutePath());

        try {
            app_A.start();
            app_B.start();
            app_C.start();

            String result = httpGetString("http://127.0.0.1:7777/serviceA/datafroma").result();

            assertEquals("data from A - data from B - data from c /computed in D/ ", result);

            app_A.stop();
            app_B.stop();
            app_C.stop();
            //check created file
            List<String> expectedLines = Files.readAllLines(Paths.get(createdFolder.listFiles()[0].toURI()));

            System.out.println(expectedLines.stream().collect(Collectors.joining(System.lineSeparator())));

        } finally {
            app_A.stop();
            app_B.stop();
            app_C.stop();
        }
    }

    @Test
    public void testTracingWithCustomLogger() throws Exception {

        DrinkWaterApplication app = DrinkWaterApplication.create("application-C-custom-logger");
        app.addServiceBuilder(new ApplicationC(true,true, CustomTraceClass.class));


        try {
            app.start();
            String result = httpGetString("http://127.0.0.1:9999/serviceC/DataFromC").result();

            assertEquals("data from c", result);

            app.stop();

            CustomTraceClass eventLogger = (CustomTraceClass)app.getCurrentBaseEventLogger();

            assertEquals(2, eventLogger.called);
        } finally {

            app.stop();
        }
    }

    @Test
    public void testTracingWithException() throws Exception {

        DrinkWaterApplication app_C = DrinkWaterApplication.create("application-withException");
        app_C.addServiceBuilder(new ApplicationC(true,true, MockEventLogger.class));

        app_C.start();

        try {
            String result = httpGetString("http://127.0.0.1:9999/serviceC/TestThrowException").result();

            MockEventLogger logger = (MockEventLogger) app_C.getCurrentBaseEventLogger();

            app_C.stop();

            assertThat(logger.count()).isEqualTo(2);
            assertThat(logger.containsAnyEventOfType(ExceptionEvent.class)).isTrue();
            assertThat(logger.containsAnyEventOfType(ServerReceivedEvent.class)).isTrue();
            assertThat(logger.containsAnyEventOfType(ServerSentEvent.class)).isFalse();
        } finally {

            app_C.stop();
        }
    }

    @Test
    public void shouldNotTraceByDefault() throws Exception {

        try (DrinkWaterApplication app = DrinkWaterApplication.create(
                options()
                        .use(TestConfiguration.class)
                        .autoStart())) {

            MockEventLogger logger = (MockEventLogger) app.getCurrentBaseEventLogger();

//            String port = (String)app.getServiceProperty("test", RestService.REST_PORT_KEY);
            String port = "";

            String result = httpGetString(String.format("http://127.0.0.1:%s/test/info", port)).result();

            assertThat(logger).isNull();
            assertThat(result).isEqualTo("test info");
        }
    }
}
