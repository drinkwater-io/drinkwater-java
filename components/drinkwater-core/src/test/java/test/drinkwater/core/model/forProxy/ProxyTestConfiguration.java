package test.drinkwater.core.model.forProxy;

import drinkwater.ApplicationBuilder;
import drinkwater.helper.SocketUtils;
import drinkwater.trace.MockEventLogger;
import test.drinkwater.core.RestService;
import test.drinkwater.core.model.ITestService;
import test.drinkwater.core.model.TestServiceImpl;

public class ProxyTestConfiguration extends ApplicationBuilder {

    public void configure() {
        String randomPortIcc = SocketUtils.freePort() + "";
        String randomPort = SocketUtils.freePort() + "";
        useTracing(true);
        useEventLogger(MockEventLogger.class);
        addService("proxyService")
                .addInitialProperty("proxy.endpoint", String.format("http://0.0.0.0:%s/icc",randomPortIcc ))
                .addInitialProperty("destination.endpoint", String.format("http://0.0.0.0:%s/test",randomPort ))
                .withProperties("classpath:proxyinfo.properties")
                .useTracing(true)
                .asHttpProxy();

        addService("test",
                ITestService.class,
                new TestServiceImpl())
                .addInitialProperty(RestService.REST_PORT_KEY, randomPort).asRest();

    }
}
