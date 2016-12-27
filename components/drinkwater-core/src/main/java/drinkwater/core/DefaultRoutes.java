package drinkwater.core;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

/**
 * Created by A406775 on 15/12/2016.
 */
@ContextName("MASTER")
public class DefaultRoutes extends RouteBuilder {

    @PropertyInject("drinkwater.application.name")
    public String appName;

    @Override
    public void configure() throws Exception {
        from("timer:foo?period=5000")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("heart beat from : " + appName);
                    }
                });
    }

}
