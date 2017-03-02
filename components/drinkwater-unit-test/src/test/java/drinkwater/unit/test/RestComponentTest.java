package drinkwater.unit.test;


import drinkwater.core.DrinkWaterApplication;
import drinkwater.helper.json.CustomJacksonObjectMapper;
import drinkwater.rest.RestService;
import drinkwater.test.HttpUnitTest;
import drinkwater.unit.test.model.forRest.FileReadResult;
import drinkwater.unit.test.model.forRest.RestConfiguration;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static drinkwater.ApplicationOptionsBuilder.options;
import static drinkwater.helper.GeneralUtils.getFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class RestComponentTest extends HttpUnitTest {

    @Test
    public void simpleTest() throws IOException {

        try (DrinkWaterApplication app = DrinkWaterApplication.create(
                options().autoStart().use(RestConfiguration.class))) {

            String port = (String) app.getComponentProperty("serviceA", RestService.REST_PORT_KEY);

            String result = httpGetString(
                    String.format("http://127.0.0.1:%s/serviceA/info/hello",
                            port)).result();

            assertThat(result).isEqualTo("pong hello");
        }
    }

    @Test
    public void testUploadFile() throws IOException {

        try (DrinkWaterApplication app = DrinkWaterApplication.create(
                options().autoStart().use(RestConfiguration.class))) {

            String file_to_upload = getFileContent("/file_to_upload.txt");
            InputStream is = new ByteArrayInputStream(file_to_upload.getBytes());

            String port = (String) app.getComponentProperty("serviceA", RestService.REST_PORT_KEY);

            FileReadResult result = httpPostFile(
                    String.format("http://127.0.0.1:%s/serviceA/upload",
                            port), is, FileReadResult.class, null).asObject();

            assertEquals("hello world uploaded", result.getContent());
        }
    }

    @Test
    public void testParameterAsMap() throws Exception {

        try (DrinkWaterApplication app = DrinkWaterApplication.create(options().autoStart().use(RestConfiguration.class))) {

            //create a map serialize and encode it
            java.util.Map<String, String> mapelements = new java.util.HashMap<String, String>();
            mapelements.put("myKey", "myValue");
            mapelements.put("mynumber", "1");
            CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
            String s = mapper.writeValueAsString(mapelements);
            s = java.net.URLEncoder.encode(s, StandardCharsets.UTF_8.toString());

            String host = String.format("http://127.0.0.1:%s",
                    app.getComponentProperty("serviceA", RestService.REST_PORT_KEY));

            //pass it as a json object
            String result = httpGetString(host + "/serviceA/methodWithMap?paramAsMap=" + s + "&another_param=someOtherParamValue").result();

            //assert
            assertEquals("paramAsMap=[(myKey:myValue)(mynumber:1)] - another_param=someOtherParamValue", result);

        }
    }
}
