package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import drinkwater.trace.MockEventLogger;
import org.junit.Test;
import test.drinkwater.core.model.forProxy.ProxyTestConfiguration;
import test.drinkwater.core.model.forProxy.SimpleTestHandler;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ProxyTest extends HttpUnitTest {

    @Test
    public void shouldProxySimpleService() throws Exception {

        try (DrinkWaterApplication proxyApp =
                     DrinkWaterApplication.create("proxy-application",options()
                                     .use(ProxyTestConfiguration.class)
                                     .autoStart())) {

            String proxyEndpoint = (String)proxyApp.getServiceProperty("proxyService", "proxy.endpoint");

            String result = httpGetString(proxyEndpoint + "/info").result();
            assertEquals("test info", result);
            result = httpGetString(proxyEndpoint + "/info?increment").result();
            assertEquals("test info", result);

            proxyApp.stop();
            MockEventLogger logger = (MockEventLogger) proxyApp.getCurrentBaseEventLogger();
            assertEquals(4, logger.getEvents().size());

            //check the handler status
            assertThat(SimpleTestHandler.increment).isEqualTo(2);

        }
    }
}
