package test.drinkwater.core.model;

import drinkwater.ApplicationBuilder;
import drinkwater.trace.MockEventLogger;

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
