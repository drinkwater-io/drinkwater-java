package drinkwater.core.internal;

import drinkwater.core.DrinkWaterApplication;
import org.apache.camel.Exchange;

public class ShutDownDrinkWaterBean {


    DrinkWaterApplication drinkWaterApp;

    public ShutDownDrinkWaterBean(DrinkWaterApplication drinkWaterApp) {
        this.drinkWaterApp = drinkWaterApp;
    }

    public void stop(Exchange exchange){

        drinkWaterApp.stop();
        System.exit(0);
    }
}
