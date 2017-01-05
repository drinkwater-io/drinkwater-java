package drinkwater.helper;

import java.util.Properties;

/**
 * Created by A406775 on 30/12/2016.
 */
public final class MapHelper {

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
}
