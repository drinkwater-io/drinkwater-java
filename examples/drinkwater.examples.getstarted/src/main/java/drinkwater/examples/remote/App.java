package drinkwater.examples.remote;

import drinkwater.ApplicationBuilder;
import drinkwater.core.main.Drinkwater;

/**
 * Hello world!
 */
public class App extends ApplicationBuilder{
    public static void main(String[] args) throws Exception {
        Drinkwater.run(App.class);
    }

    @Override
    public void configure() {
        addService("test", ISimpleService.class, SimpleServiceImpl.class).asRest();
    }
}
