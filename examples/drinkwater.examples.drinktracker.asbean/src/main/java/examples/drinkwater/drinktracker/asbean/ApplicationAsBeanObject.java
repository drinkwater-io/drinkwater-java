package examples.drinkwater.drinktracker.asbean;

import baseline.BaseLinefactory;
import drinkwater.ApplicationBuilder;

public class ApplicationAsBeanObject extends ApplicationBuilder {

    @Override
    public void configure() {
        addConfigurations(BaseLinefactory.createServices());
    }
}
