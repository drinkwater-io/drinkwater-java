package drinkwater.test;

import drinkwater.helper.json.CustomJacksonObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.when;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;

/**
 * Created by A406775 on 12/01/2017.
 */
public class TestHelper {

    public static String getFileContent(String resourceFilePath) throws IOException {

        ClassLoader classLoader = TestHelper.class.getClassLoader();

        File file = new File(classLoader.getResource(resourceFilePath).getFile());

        String content = new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));

        return content;
    }

    public static byte[] getFileAsBytes(String resourceFilePath) throws IOException {

        ClassLoader classLoader = TestHelper.class.getClassLoader();

        File file = new File(classLoader.getResource(resourceFilePath).getFile());

        return  Files.readAllBytes(file.toPath());
    }

    public static void compareJson(String expected, String actual) {

        assertJsonEquals(rs(expected), actual, when(IGNORING_ARRAY_ORDER));


    }

    public static <T> T fromJsonString(String s, Class clazz) throws IOException {
        CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
        return (T) mapper.readValue(s, clazz);
    }

    public static String toJsonString(Object obj) throws IOException {
        CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    public static String rs(String s) {
        String answer = s.replaceAll("'", "\"");
        return answer;
    }


}
