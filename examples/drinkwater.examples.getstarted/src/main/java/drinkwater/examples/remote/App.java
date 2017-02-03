package drinkwater.examples.remote;

import drinkwater.ApplicationBuilder;
import drinkwater.core.main.Main;

/**
 * Hello world!
 */
public class App extends ApplicationBuilder{
    public static void main(String[] args) throws Exception {
        new Main(new App()).run();
    }

    @Override
    public void configure() {
        addService("test", ISimpleService.class, SimpleServiceImpl.class).asRest();
    }
}
