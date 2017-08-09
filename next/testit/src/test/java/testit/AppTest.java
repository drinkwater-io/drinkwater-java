package testit;

import next.guice.GuiceInjectorAdapter;
import next.hosts.ConsoleHost;
import next.injector.CdiInjectorAdpater;
import org.junit.Test;
import testit.hosts.GuiceTestModule;
import testit.hosts.Startup;

/**
 * Created by A406775 on 24/03/2017.
 */
public class AppTest {

    @Test
    public void simpleTest() throws Exception{
        //ConsoleHost.run(Startup.class, new GuiceInjectorAdapter(new GuiceTestModule()));
        ConsoleHost.run(Startup.class, new CdiInjectorAdpater());
    }
}
