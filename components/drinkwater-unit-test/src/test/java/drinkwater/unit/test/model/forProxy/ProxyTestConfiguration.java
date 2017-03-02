package drinkwater.unit.test.model.forProxy;

import drinkwater.ApplicationBuilder;
import drinkwater.feature.trace.TraceFeature;
import drinkwater.helper.SocketUtils;
import drinkwater.http.proxy.HttpProxyComponent;
import drinkwater.http.proxy.HttpProxyServiceBuilder;
import drinkwater.rest.RestComponent;
import drinkwater.rest.RestServiceBuilder;
import drinkwater.test.samples.ISimpleTestService;
import drinkwater.test.samples.SimpleTestServiceImpl;

public class ProxyTestConfiguration extends ApplicationBuilder {

    public void configure() {
//        useTracing(true);
//        useEventLogger(MockEventLogger.class);

        String randomPortIcc = SocketUtils.freePort() + "";
        String randomPort = SocketUtils.freePort() + "";

        expose(ISimpleTestService.class)
                .as(HttpProxyComponent.class)
                .named("proxyService")
                .with(HttpProxyServiceBuilder.FrontEndPoint,String.format("http://0.0.0.0:%s/icc",randomPortIcc ) )
                .with(HttpProxyServiceBuilder.DestintationEndpoint,String.format("http://0.0.0.0:%s/test",randomPort ))
                .use(TraceFeature.class);

        expose(SimpleTestServiceImpl.class)
                .as(RestComponent.class)
                .named("test")
                .with(RestServiceBuilder.REST_PORT_KEY, randomPort);


//        addService("proxyService")
//                .addInitialProperty("proxy.endpoint", String.format("http://0.0.0.0:%s/icc",randomPortIcc ))
//                .addInitialProperty("destination.endpoint", String.format("http://0.0.0.0:%s/test",randomPort ))
//                .withProperties("classpath:proxyinfo.properties")
//                .useTracing(true)
//                .asHttpProxy();
//
//        addService("test",
//                ITestService.class,
//                new TestServiceImpl())
//                .addInitialProperty(RestService.REST_PORT_KEY, randomPort).asRest();

    }
}
