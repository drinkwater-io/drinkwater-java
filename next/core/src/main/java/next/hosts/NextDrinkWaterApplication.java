package next.hosts;

import next.api.INextApplicationBuilder;
import org.apache.camel.CamelContext;

import javax.inject.Inject;

/**
 * Created by A406775 on 24/03/2017.
 */
public class NextDrinkWaterApplication {
    private INextApplicationBuilder builder;
    private CamelContext ctx;

    @Inject
    public NextDrinkWaterApplication(INextApplicationBuilder builder) {
        this.builder = builder;
    }

    public void test(){
        System.out.println("test " + builder);
    }
}
