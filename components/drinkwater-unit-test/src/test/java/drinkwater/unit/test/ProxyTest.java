package drinkwater.unit.test;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.feature.trace.TraceFeature;
import drinkwater.test.HttpUnitTest;
import drinkwater.trace.MockEventLogger;
import drinkwater.unit.test.model.forProxy.ProxyMultipartTestApplicationBuilder;
import drinkwater.unit.test.model.forProxy.ProxyTestConfiguration;
import drinkwater.unit.test.model.forProxy.SimpleTestHandler;
import drinkwater.unit.test.model.forRest.FileReadResult;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static drinkwater.ApplicationOptionsBuilder.options;
import static drinkwater.helper.GeneralUtils.getFileContent;
import static org.assertj.core.api.Assertions.assertThat;

public class ProxyTest extends HttpUnitTest {

    @Test
    public void shouldProxySimpleService() throws Exception {

        try (DrinkWaterApplication proxyApp =
                     DrinkWaterApplication.create("proxy-application",options()
                                     .use(ProxyTestConfiguration.class)
                                     .autoStart())) {

            String proxyEndpoint =
                    (String)proxyApp.getComponentProperty("proxyService", "proxy.endpoint");

            String result = httpGetString(proxyEndpoint + "/echo?message=helloyou").result();
            assertThat(result).isEqualTo("helloyou");
            result = httpGetString(proxyEndpoint + "/echo?message=helloagain&increment").result();
            assertThat(result).isEqualTo("helloagain");

            proxyApp.stop();
            MockEventLogger logger = (MockEventLogger)proxyApp.getFeature("proxyService", TraceFeature.class).getLogger();
            assertThat(logger.getEvents().size()).isEqualTo(4);

            //check the handler status
            assertThat(SimpleTestHandler.increment).isEqualTo(2);

        }
    }
//
    @Test
    public void shouldProxyWithMultipart() throws Exception {
        try (DrinkWaterApplication proxyApp =
                     DrinkWaterApplication.create("proxy-multipart-test",options()
                             .use(ProxyMultipartTestApplicationBuilder.class)
                             .autoStart())) {

            String proxyEndpoint =
                    (String)proxyApp.getComponentProperty("proxyService", "proxy.endpoint");

            String file_to_upload = getFileContent("/file_to_upload.txt");
            InputStream is = new ByteArrayInputStream(file_to_upload.getBytes());

            FileReadResult result = httpPostFile(proxyEndpoint + "/upload", is, FileReadResult.class, null)
                   .asObject();


            assertThat(result.getContent()).isEqualTo("hello world uploaded");

            proxyApp.stop();

        }
    }



}
