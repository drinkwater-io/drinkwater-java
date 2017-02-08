package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import drinkwater.trace.MockEventLogger;
import org.junit.Test;
import test.drinkwater.core.model.ProxyTestConfiguration;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.junit.Assert.assertEquals;

public class ProxyTest extends HttpUnitTest {

    @Test
    public void shouldProxySimpleService() throws Exception {

        try (DrinkWaterApplication proxyApp =
                     DrinkWaterApplication.create("proxy-application",options()
                                     .use(ProxyTestConfiguration.class)
                                     .autoStart())) {

            String result = httpGetString("http://127.0.0.1:7777/icc/info").result();
            assertEquals("test info", result);
            result = httpGetString("http://127.0.0.1:7777/icc/info").result();
            assertEquals("test info", result);

            Thread.sleep(800);

            MockEventLogger logger = (MockEventLogger) proxyApp.getCurrentBaseEventLogger();

            assertEquals(4, logger.getEvents().size());

        }
    }
}
