package drinkwater.examples.scheduler;

import drinkwater.ApplicationBuilder;

/**
 * Hello world!
 */
public class HelloWorldSchedulerConfiguration extends ApplicationBuilder {
    @Override
    public void configure() {
        addService("helloStore", IHelloHolder.class, new HelloHolderImpl());
        addService("printHelloJob", IPrintHelloWorlds.class, PrintHelloWorldsImpl.class, "helloStore").repeat(500);
    }
}
