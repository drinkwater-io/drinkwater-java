package test.drinkwater.core.model;

import drinkwater.ApplicationBuilder;

public class ProxyTestConfiguration extends ApplicationBuilder {

    public void configure() {
        addService("proxyService").
                withProperties("classpath:proxyinfo.properties")
                .useTracing(true)
                .asHttpProxy();

        addService("test",
                ITestService.class,
                new TestServiceImpl()).asRest();
    }
}
