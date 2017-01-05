package drinkwater.examples.scheduler;

import drinkwater.ServiceConfigurationBuilder;

/**
 * Hello world!
 */
public class HelloWorldSchedulerConfiguration extends ServiceConfigurationBuilder {
    @Override
    public void configure() {
        addService("helloStore", IHelloHolder.class, new HelloHolderImpl());
        addService("printHelloJob", IPrintHelloWorlds.class, PrintHelloWorldsImpl.class, "helloStore").repeat(500);
    }
}
