package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.test.HttpUnitTest;
import org.junit.Test;
import test.drinkwater.core.model.forRest.FileReadResult;
import test.drinkwater.core.model.forRest.RestConfiguration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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
}
