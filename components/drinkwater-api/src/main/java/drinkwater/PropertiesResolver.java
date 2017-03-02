package drinkwater;

import org.apache.camel.util.IOHelper;
import org.apache.camel.util.ObjectHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import static drinkwater.helper.MapUtils.prefixProperties;

public class PropertiesResolver {

//    private IPropertiesAware propLocator;

    private boolean ignoreMissingLocation = true;

    public PropertiesResolver(){
    }

    public synchronized Properties resolveProperties(String[] propertiesLocations) throws Exception {
        Properties props = resolveProperties(ignoreMissingLocation,propertiesLocations);

       // props = prefixProperties(prefix, props);

        return props;
    }

    private Properties resolveProperties(boolean ignoreMissingLocation, String... uri) throws Exception {
        Properties answer = new Properties();

        for (String path : uri) {
           if (path.startsWith("file:")) {
                Properties prop = loadPropertiesFromFilePath(ignoreMissingLocation, path, StandardCharsets.UTF_8.toString());
                prop = prepareLoadedProperties(prop);
                answer.putAll(prop);
            } else {
                // default to classpath
                Properties prop = loadPropertiesFromClasspath(ignoreMissingLocation, path, StandardCharsets.UTF_8.toString());
                prop = prepareLoadedProperties(prop);
                answer.putAll(prop);
            }
        }

        return answer;
    }

    private Properties loadPropertiesFromFilePath(boolean ignoreMissingLocation, String path, String encoding) throws IOException {
        Properties answer = new Properties();

        if (path.startsWith("file:")) {
            path = ObjectHelper.after(path, "file:");
        }

        InputStream is = null;
        Reader reader = null;
        try {
            is = new FileInputStream(path);
            if (encoding != null) {
                reader = new BufferedReader(new InputStreamReader(is, encoding));
                answer.load(reader);
            } else {
                answer.load(is);
            }
        } catch (FileNotFoundException e) {
            if (!ignoreMissingLocation) {
                throw e;
            }
        } finally {
            IOHelper.close(reader, is);
        }

        return answer;
    }

    private Properties loadPropertiesFromClasspath(boolean ignoreMissingLocation, String path, String encoding) throws IOException {
        Properties answer = new Properties();

        if (path.startsWith("classpath:")) {
            path = ObjectHelper.after(path, "classpath:");
        }
        if(!path.startsWith("/")){
            path = "/" + path;
        }

        InputStream is = PropertiesResolver.class.getResourceAsStream(path);
        Reader reader = null;
        if (is == null) {
            if (!ignoreMissingLocation) {
                throw new FileNotFoundException("Properties file " + path + " not found in classpath");
            }
        } else {
            try {
                if (encoding != null) {
                    reader = new BufferedReader(new InputStreamReader(is, encoding));
                    answer.load(reader);
                } else {
                    answer.load(is);
                }
            } finally {
                IOHelper.close(reader, is);
            }
        }
        return answer;
    }

    private Properties prepareLoadedProperties(Properties properties) {
        Properties answer = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                String s = (String) value;

                // trim any trailing spaces which can be a problem when loading from
                // a properties file, note that java.util.Properties does already this
                // for any potential leading spaces so there's nothing to do there
                value = trimTrailingWhitespaces(s);
            }
            answer.put(key, value);
        }
        return answer;
    }

    private static String trimTrailingWhitespaces(String s) {
        int endIndex = s.length();
        for (int index = s.length() - 1; index >= 0; index--) {
            if (s.charAt(index) == ' ') {
                endIndex = index;
            } else {
                break;
            }
        }
        String answer = s.substring(0, endIndex);
        return answer;
    }
}
