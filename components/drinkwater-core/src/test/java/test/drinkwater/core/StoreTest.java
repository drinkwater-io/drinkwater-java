package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import org.junit.Test;
import test.drinkwater.core.model.forStore.ISimpleDataStoreDependentService;
import test.drinkwater.core.model.forStore.StoreApplication;

import java.io.IOException;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.assertj.core.api.Assertions.assertThat;

public class StoreTest {

    @Test
    public void shouldStartSimpleStore() throws IOException {

        try (DrinkWaterApplication app =
                     DrinkWaterApplication
                             .create("store-test", options().use(StoreApplication.class)
                             .autoStart())){

            ISimpleDataStoreDependentService s = app.getService("store-dependent-service");

            assertThat(s.getStoreInfo()).isEqualTo("test.drinkwater.core.model.forStore.SimpleTestStore");
        }
    }
}
