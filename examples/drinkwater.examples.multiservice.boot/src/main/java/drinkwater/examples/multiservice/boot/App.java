package drinkwater.examples.multiservice.boot;


import drinkwater.core.main.Main;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        System.out.println( "Starting multi service sample" );
        Main main = new Main(new MultiServiceConfiguration());
        main.run();
    }
}
