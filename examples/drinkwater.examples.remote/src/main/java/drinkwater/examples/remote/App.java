package drinkwater.examples.remote;

import drinkwater.core.main.Main;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting multi service sample");
        Main main = new Main("multiservice", new MultiServiceRemoteConfiguration());
        main.run();
    }
}
