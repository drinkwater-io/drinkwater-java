package drinkwater.examples.numberservice.asbean;

import drinkwater.boot.DrinkWaterBoot;
import drinkwater.core.DrinkWaterApplicationConfig;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import drinkwater.core.ServiceConfigurationCollection;
import drinkwater.examples.numberservice.*;

import javax.enterprise.inject.Produces;

/**
 * Hello world!
 *
 */

public class BootApp
{
    public static void main( String[] args ) throws Exception {
        DrinkWaterBoot booter = new DrinkWaterBoot();
        booter.run();
    }
}
