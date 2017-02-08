package examples.drinkwater.drinktracker.asrest;

import drinkwater.core.main.Drinkwater;
import drinkwater.trace.ConsoleEventLogger;

import static drinkwater.ApplicationOptionsBuilder.options;

public class Main {
    public static void main(String[] args) throws Exception {
        Drinkwater.run("test-rest",
                options()
                        .use(ApplicationAsRest.class)
                        .use(ConsoleEventLogger.class));
    }
}
