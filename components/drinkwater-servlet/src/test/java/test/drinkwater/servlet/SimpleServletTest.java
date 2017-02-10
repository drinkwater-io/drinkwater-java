package test.drinkwater.servlet;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.servlet.DrinkWaterServletContextListener;
import drinkwater.test.ServletUnitTest;
import drinkwater.trace.MockEventLogger;
import org.junit.Test;

import static drinkwater.test.HttpUnitTest.httpGetString;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleServletTest extends ServletUnitTest {

    protected String getConfiguration() {
        return "/test1-web.xml";
    }

    @Test
    public void testgetDWApplication() throws Exception {
        DrinkWaterApplication application = getDrinkWaterApplication();
        assertThat(application).isNotNull();

        String result = httpGetString("http://localhost:8889/test/echo?message=testmessage").result();
        assertThat(result).isEqualTo("testmessage");

        Thread.sleep(100);

        //check access to properties
        String fromProps = application.safeLookupProperty(String.class, "hello", "nodefault");
        assertThat(fromProps).isEqualTo("world");

        //check logs
        MockEventLogger logger = (MockEventLogger) application.getCurrentBaseEventLogger();
        assertThat(logger.getEvents().size()).isEqualTo(2);

    }

    public DrinkWaterApplication getDrinkWaterApplication() {
        return DrinkWaterServletContextListener.instance;
    }
}
