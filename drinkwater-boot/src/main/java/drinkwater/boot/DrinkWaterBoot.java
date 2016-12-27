package drinkwater.boot;

import org.apache.camel.CamelContext;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.builder.DefaultFluentProducerTemplate;

import java.util.List;


/**
 * Hello world!
 *
 */
public class DrinkWaterBoot {

    private DrinkWaterMain drinkWaterMain;

    public DrinkWaterBoot() {
        drinkWaterMain = new DrinkWaterMain();
    }

    public void run() throws Exception {
        drinkWaterMain.run();
    }

    public void runFor(int millis) throws Exception {
        drinkWaterMain.setDuration(millis);
        this.run();
    }

    public void start() throws Exception {
        drinkWaterMain.start();

    }

    public void stop() throws Exception {
        drinkWaterMain.stop();
    }

    public FluentProducerTemplate getTemplate(){
        List<CamelContext> contexts = drinkWaterMain.getCamelContexts();
        FluentProducerTemplate fpt =  DefaultFluentProducerTemplate.on(contexts.get(0));
        return fpt;
    }

}
