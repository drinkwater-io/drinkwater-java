package test.drinkwater.core.model;

import drinkwater.ServiceConfigurationBuilder;

public class ProxyTestConfiguration extends ServiceConfigurationBuilder {

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
