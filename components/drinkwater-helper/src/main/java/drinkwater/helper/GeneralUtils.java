package drinkwater.helper;

import drinkwater.helper.json.CustomJacksonObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class GeneralUtils {
    public static Path getJarFolderPath(Class clazzOrigin) {
        if (clazzOrigin == null) {
            clazzOrigin = GeneralUtils.class;
        }

        try {
            Path path = Paths.get(clazzOrigin.getProtectionDomain().getCodeSource().getLocation().toURI());
            return path;
        } catch (Exception e) {
            throw new RuntimeException("could not get the jar folder", e);
        }

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

        ClassLoader classLoader = GeneralUtils.class.getClassLoader();

        File file = new File(classLoader.getResource(resourceFilePath).getFile());

        String content = new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));

        return content;
    }

    public static Properties loadResourceFromClassPath(Class clazzOrigin, String resourceFile) throws IOException {
        final Properties properties = new Properties();
        try (final InputStream stream = clazzOrigin.getResourceAsStream(resourceFile)) {
            properties.load(stream);

            return properties;
        }
    }
}
