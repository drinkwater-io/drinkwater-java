package test.drinkwater.core.model.forProxy;

import drinkwater.ApplicationBuilder;
import drinkwater.helper.SocketUtils;
import drinkwater.rest.RestService;
import test.drinkwater.core.model.forRest.IServiceA;
import test.drinkwater.core.model.forRest.ServiceAImpl;

/**
 * Created by a628979 on 20/02/2017.
 */
public class ProxyMultipartTestApplicationBuilder extends ApplicationBuilder {

    public void configure() {
        String randomPortIcc = SocketUtils.freePort() + "";
        String randomPort = SocketUtils.freePort() + "";
        addService("proxyService")
                .addInitialProperty("proxy.endpoint", String.format("http://0.0.0.0:%s/icc", randomPortIcc))
                .addInitialProperty("destination.endpoint", String.format("http://0.0.0.0:%s/test", randomPort))
                .asHttpProxy();

        addService("test",
                IServiceA.class,
                new ServiceAImpl())
                .addInitialProperty(RestService.REST_PORT_KEY, randomPort)
                .asRest();
    }

}
