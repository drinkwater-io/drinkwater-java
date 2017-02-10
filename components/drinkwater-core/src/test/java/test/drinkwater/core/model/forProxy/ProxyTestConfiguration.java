package test.drinkwater.core.model.forProxy;

import drinkwater.ApplicationBuilder;
import drinkwater.trace.MockEventLogger;
import test.drinkwater.core.model.ITestService;
import test.drinkwater.core.model.TestServiceImpl;

public class ProxyTestConfiguration extends ApplicationBuilder {

    public void configure() {
        useTracing(true);
        useEventLogger(MockEventLogger.class);
        addService("proxyService").
                withProperties("classpath:proxyinfo.properties")
                .useTracing(true)
                .asHttpProxy();

        addService("test",
                ITestService.class,
                new TestServiceImpl()).asRest();
    }
}
