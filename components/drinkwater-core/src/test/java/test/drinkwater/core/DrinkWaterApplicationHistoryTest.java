package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.internal.DrinkWaterApplicationHistory;
import org.junit.Test;
import test.drinkwater.core.model.ITestService;
import test.drinkwater.core.model.TestConfiguration;
import test.drinkwater.core.model.TestServiceImpl;

import java.io.IOException;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by A406775 on 4/01/2017.
 */
public class DrinkWaterApplicationHistoryTest {

    @Test
    public void shouldTakeSnapShotsAndRetainHistory() throws IOException {

        try (DrinkWaterApplication app = DrinkWaterApplication.create("core-test", options()
                .use(TestConfiguration.class)
                .autoStart())) {

            DrinkWaterApplicationHistory history = app.takeSnapShot();
            assertTrue(app.history().size() == 1);
            history = app.takeSnapShot();
            assertTrue(app.history().size() == 2);
        }
    }

    @Test
    public void shouldRevertToPreviousState() throws IOException {
        try (DrinkWaterApplication app = DrinkWaterApplication.create("core-test", options()
                .use(TestConfiguration.class)
                .autoStart())) {

            ITestService testService = app.getService("test");
            assertEquals("test info", testService.getInfo());

            app.takeSnapShot();

            //change the service
            app.patchService("test", new TestServiceImpl("new Info"));
            testService = app.getService("test");
            assertEquals("new Info", testService.getInfo());

            //revert to last config
            app.revertState();
            testService = app.getService("test");
            assertEquals("test info", testService.getInfo());

        }
    }
}
