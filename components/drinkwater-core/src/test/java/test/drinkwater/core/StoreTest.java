package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import org.junit.Test;
import test.drinkwater.core.model.forStore.StoreServiceConfiguration;

import java.io.IOException;

public class StoreTest {

    @Test
    public void shouldStartSimpleStore() throws IOException {

        DrinkWaterApplication app = DrinkWaterApplication.create("store-test", false);
        try {
            app.addServiceBuilder(new StoreServiceConfiguration());
            app.start();

            System.out.println("do something here");
        }finally {

            app.stop();
        }
    }
}
