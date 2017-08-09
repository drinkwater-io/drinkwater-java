package testit.hosts;

import com.google.inject.AbstractModule;
import next.api.IContainer;
import next.api.INextApplicationBuilder;
import next.guice.GuiceContainer;
import next.hosts.DefaultApplicationBuilder;

/**
 * Created by A406775 on 24/03/2017.
 */
public class GuiceTestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NextSimpleService.class);
    }
}
