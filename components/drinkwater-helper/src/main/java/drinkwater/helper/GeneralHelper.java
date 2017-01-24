package drinkwater.helper;

import drinkwater.helper.json.CustomJacksonObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class GeneralHelper {
    public static String getJarFolder() {
        String s = GeneralHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        s = s.substring(0, s.lastIndexOf("/")+ 1);

//        Path path = Paths.get();
//        String url = path.getParent().toString() ;
        String decodedPath = null;
        try {
            decodedPath = URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return decodedPath;
    }

    public static <T> T fromJsonString(String s, Class clazz) throws IOException {
        CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
        return (T) mapper.readValue(s, clazz);
    }

    public static String toJsonString(Object obj) throws IOException {
        CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    public static String getFileContent(String resourceFilePath) throws IOException {

        ClassLoader classLoader = GeneralHelper.class.getClassLoader();

        File file = new File(classLoader.getResource(resourceFilePath).getFile());

        String content = new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));

        return content;
    }
}
