package drinkwater.examples.scheduler;

/**
 * Created by A406775 on 5/01/2017.
 */
public class PrintHelloWorldsImpl implements IPrintHelloWorlds {

    IHelloHolder helloHolder;

    private int counter = 0;

    @Override
    public void printHelloWorld() {

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String value = " you - " + counter++;
        System.out.println(value);
        helloHolder.addHello(value);
    }
}
