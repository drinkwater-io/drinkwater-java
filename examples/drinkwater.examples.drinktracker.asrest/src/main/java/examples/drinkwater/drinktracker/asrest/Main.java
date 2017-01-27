package examples.drinkwater.drinktracker.asrest;

import drinkwater.trace.ConsoleEventLogger;

public class Main {
    public static void main(String[] args) throws Exception {
        drinkwater.core.main.Main app = new drinkwater.core.main.Main(
                "test-rest",
                new ServiceConfigurationAsRest(),
                ConsoleEventLogger.class,
                true, false);
        app.run();

    }
}
