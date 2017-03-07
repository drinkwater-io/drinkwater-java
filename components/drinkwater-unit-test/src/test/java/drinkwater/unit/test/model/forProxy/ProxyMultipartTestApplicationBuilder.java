package drinkwater.unit.test.model.forProxy;

import drinkwater.ApplicationBuilder;
import drinkwater.helper.SocketUtils;
import drinkwater.http.proxy.HttpProxyComponent;
import drinkwater.http.proxy.HttpProxyServiceBuilder;
import drinkwater.rest.RestComponent;
import drinkwater.rest.RestServiceBuilder;
import drinkwater.test.samples.ISimpleTestService;
import drinkwater.test.samples.SimpleTestServiceImpl;
import drinkwater.trace.MockEventLogger;
import drinkwater.unit.test.model.forRest.ServiceAImpl;

import static drinkwater.feature.multipart.MultiPartFeature.multipart;
import static drinkwater.feature.trace.TraceFeatureBuilder.trace;
//import drinkwater.helper.SocketUtils;
//import drinkwater.rest.RestService;
//import test.drinkwater.core.model.forRest.IServiceA;
//import test.drinkwater.core.model.forRest.ServiceAImpl;

/**
 * Created by a628979 on 20/02/2017.
 */
public class ProxyMultipartTestApplicationBuilder extends ApplicationBuilder {

    public void configure() {
        String randomPortIcc = SocketUtils.freePort() + "";
        String randomPort = SocketUtils.freePort() + "";

        expose(ServiceAImpl.class)
                .as(HttpProxyComponent.class)
                .named("proxyService")
                .with(HttpProxyServiceBuilder.FrontEndPoint, String.format("http://0.0.0.0:%s/icc", randomPortIcc))
                .with(HttpProxyServiceBuilder.DestintationEndpoint, String.format("http://0.0.0.0:%s/test", randomPort))
        ;

        expose(ServiceAImpl.class)
                .as(RestComponent.class)
                .named("test")
                .with(RestServiceBuilder.REST_PORT_KEY, randomPort)
                .use(multipart());

//        addService("proxyService")
//                .addInitialProperty("proxy.endpoint", String.format("http://0.0.0.0:%s/icc", randomPortIcc))
//                .addInitialProperty("destination.endpoint", String.format("http://0.0.0.0:%s/test", randomPort))
//                .asHttpProxy();
//
//        addService("test",
//                IServiceA.class,
//                new ServiceAImpl())
//                .addInitialProperty(RestService.REST_PORT_KEY, randomPort)
//                .asRest();
    }

}
