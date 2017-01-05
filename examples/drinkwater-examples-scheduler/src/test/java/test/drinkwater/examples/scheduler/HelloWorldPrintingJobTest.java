package test.drinkwater.examples.scheduler;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.examples.scheduler.HelloWorldSchedulerConfiguration;
import drinkwater.examples.scheduler.IHelloHolder;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class HelloWorldPrintingJobTest {

    @Test
    public void test() throws InterruptedException {
        DrinkWaterApplication app = DrinkWaterApplication.create("scheduler-app-sample");
        app.addServiceBuilder(new HelloWorldSchedulerConfiguration());
        app.start();
        Thread.sleep(2000); // let it run for 2 sec
        app.stop();

        IHelloHolder holder = app.getService("helloStore");

        List hellos = holder.getHellos();

        assertTrue(3 < hellos.size());


    }
}
