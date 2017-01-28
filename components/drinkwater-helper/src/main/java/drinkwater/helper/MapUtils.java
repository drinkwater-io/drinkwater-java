package drinkwater.helper;

import java.util.Properties;
import java.util.Set;

/**
 * Created by A406775 on 30/12/2016.
 */
public final class MapUtils {

//    public static Map<String, String> mapOf(Tuple2<String, String>){
//
//    }

    public static Properties mergeProperties(Properties properties, Properties withProperties) {

        if (properties == null) {
            properties = new Properties();
        }

        if (withProperties == null) {
            return properties;
        }

        for (Object key : withProperties.keySet()
                ) {
            if (properties.containsKey(key)) {
                properties.remove(key);
            }
            properties.put(key, withProperties.get(key));
        }

        return properties;
    }

    public static Properties prefixProperties(String prefix, Properties properties) {

        if (properties == null || prefix == null) {
            return properties;
        }

        final Properties answer = new Properties();

        final Set<Object> keys = properties.keySet();

        for (Object key : keys
                ) {
            Object value = properties.get(key);

            answer.put(prefix + "." + key, value);
        }

        return answer;
    }


}
