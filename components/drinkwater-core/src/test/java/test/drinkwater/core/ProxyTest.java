package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import drinkwater.trace.MockEventLogger;
import org.junit.Test;
import test.drinkwater.core.model.ProxyTestConfiguration;

import static org.junit.Assert.assertEquals;

public class ProxyTest extends HttpUnitTest {

    @Test
    public void shouldProxySimpleService() throws Exception {

        DrinkWaterApplication proxyApp = DrinkWaterApplication.create("proxy-application", false, true);
        proxyApp.addServiceBuilder(new ProxyTestConfiguration());
        proxyApp.setEventLoggerClass(MockEventLogger.class);

        try {
            proxyApp.start();

            String result = httpGetString("http://127.0.0.1:7777/icc/info").result();
            assertEquals("test info", result);
            result = httpGetString("http://127.0.0.1:7777/icc/info").result();
            assertEquals("test info", result);

            Thread.sleep(400);

            MockEventLogger logger = (MockEventLogger) proxyApp.getCurrentBaseEventLogger();

            Thread.sleep(100);

            assertEquals(logger.getEvents().size(), 4);

        }
        finally {
            proxyApp.stop();
        }
    }
}
