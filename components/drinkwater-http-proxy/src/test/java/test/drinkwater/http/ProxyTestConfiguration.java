package test.drinkwater.http;

import drinkwater.ApplicationBuilder;
import drinkwater.helper.SocketUtils;
import drinkwater.http.proxy.HttpProxyComponent;
import drinkwater.http.proxy.HttpProxyServiceBuilder;
import drinkwater.rest.RestComponent;
import drinkwater.rest.RestService;
import drinkwater.rest.RestServiceBuilder;
import drinkwater.test.samples.ISimpleTestService;
import drinkwater.test.samples.SimpleTestServiceImpl;
import drinkwater.trace.MockEventLogger;

public class ProxyTestConfiguration extends ApplicationBuilder {

    public void configure() {

        String randomPortIcc = SocketUtils.freePort() + "";
        String randomPort = SocketUtils.freePort() + "";

        expose(ISimpleTestService.class)
                .as(HttpProxyComponent.class)
                .named("proxyService")
                .with(HttpProxyServiceBuilder.FrontEndPoint,String.format("http://0.0.0.0:%s/icc",randomPortIcc ) )
                .with(HttpProxyServiceBuilder.DestintationEndpoint,String.format("http://0.0.0.0:%s/test",randomPort ));

        expose(SimpleTestServiceImpl.class)
                .as(RestComponent.class)
                .named("test")
                .with(RestServiceBuilder.REST_PORT_KEY, randomPort);

//        addService("test",
//                ITestService.class,
//                new TestServiceImpl())
//                .addInitialProperty(RestService.REST_PORT_KEY, randomPort).asRest();

    }
}

