package next.guice;

import com.google.inject.AbstractModule;
import next.api.IContainer;
import next.api.INextApplicationBuilder;
import next.hosts.DefaultApplicationBuilder;

/**
 * Created by A406775 on 24/03/2017.
 */
public class DrinkWaterModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(INextApplicationBuilder.class).to(DefaultApplicationBuilder.class);
        bind(IContainer.class).to(GuiceContainer.class);
    }
}
