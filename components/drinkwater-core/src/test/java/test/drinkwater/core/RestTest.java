package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.helper.json.CustomJacksonObjectMapper;
import drinkwater.test.HttpUnitTest;
import org.junit.Test;
import test.drinkwater.core.model.forRest.FileReadResult;
import test.drinkwater.core.model.forRest.RestConfiguration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static drinkwater.test.TestHelper.getFileContent;
import static org.junit.Assert.assertEquals;

public class RestTest extends HttpUnitTest{

    @Test
    public void testUploadFile() throws IOException {

        DrinkWaterApplication app = DrinkWaterApplication.create("rest-test", false);
        app.addServiceBuilder(new RestConfiguration());
        app.start();

        String file_to_upload = getFileContent("file_to_upload.txt");
        InputStream is = new ByteArrayInputStream( file_to_upload.getBytes() );

        FileReadResult result = httpPostFile("http://127.0.0.1:8889/serviceA/upload", is, FileReadResult.class, null).asObject();

        assertEquals("hello world uploaded", result.getContent());

        app.stop();
    }

    @Test
    public void testParameterAsMap() throws Exception {

        DrinkWaterApplication app = DrinkWaterApplication.create("rest-test", false);
        app.addServiceBuilder(new RestConfiguration());
        app.start();

        //create a map serialize and encode it
        java.util.Map<String, String> mapelements = new java.util.HashMap<String, String>();
        mapelements.put("myKey", "myValue");
        mapelements.put("mynumber", "1");
        CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
        String s = mapper.writeValueAsString(mapelements);
        s = java.net.URLEncoder.encode(s, StandardCharsets.UTF_8.toString());

        //pass it as a json object
        String result = httpGetString("http://127.0.0.1:8889/serviceA/methodWithMap?paramAsMap=" + s+ "&another_param=someOtherParamValue").result();

        //assert
        assertEquals("paramAsMap=[(myKey:myValue)(mynumber:1)] - another_param=someOtherParamValue", result);

        app.stop();
    }
}
