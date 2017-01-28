package drinkwater.test;

import drinkwater.helper.GeneralUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.when;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;

/**
 * Created by A406775 on 12/01/2017.
 */
public class TestHelper {

    //TODO make some check to inform of invalid path
    //example not starting with slash
    public static String getFileContent(String resourceFilePath) throws IOException {

        return GeneralUtils.getFileContent(resourceFilePath);
//        ClassLoader classLoader = TestHelper.class.getClassLoader();
//
//        File file = new File(classLoader.getResource(resourceFilePath).getFile());
//
//        String content = new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
//
//        return content;
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
        return GeneralUtils.fromJsonString(s, clazz);
    }

    public static String toJsonString(Object obj) throws IOException {
        return GeneralUtils.toJsonString(obj);
    }

    public static String rs(String s) {
        String answer = s.replaceAll("'", "\"");
        return answer;
    }




}
