package drinkwater.examples.multiservice.boot;


import drinkwater.core.main.Drinkwater;

import static drinkwater.ApplicationOptionsBuilder.options;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        Drinkwater.run("multiservice", options().use(MultiServiceApplication.class));
    }
}
