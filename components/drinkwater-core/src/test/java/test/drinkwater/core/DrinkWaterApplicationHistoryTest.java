package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.internal.DrinkWaterApplicationHistory;
import org.junit.Test;
import test.drinkwater.core.model.ITestService;
import test.drinkwater.core.model.TestConfiguration;
import test.drinkwater.core.model.TestServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by A406775 on 4/01/2017.
 */
public class DrinkWaterApplicationHistoryTest {

    @Test
    public void shouldTakeSnapShotsAndRetainHistory() {
        DrinkWaterApplication app = DrinkWaterApplication.create("core-test", false);
        app.addServiceBuilder(new TestConfiguration());
        app.start();

        DrinkWaterApplicationHistory history = app.takeSnapShot();
        assertTrue(app.history().size() == 1);
        history = app.takeSnapShot();
        assertTrue(app.history().size() == 2);

        app.stop();
    }

    @Test
    public void shouldRevertToPreviousState() {
        DrinkWaterApplication app = DrinkWaterApplication.create("core-test", false);
        app.addServiceBuilder(new TestConfiguration());
        app.start();

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

        app.stop();
    }
}
