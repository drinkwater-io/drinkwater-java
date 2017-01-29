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
            String value = (String)properties.get(key);
            value = replaceExpression(prefix, (String)key, value);
            answer.put(prefix + "." + key, value);
        }

        return answer;
    }

    private static String replaceExpression(String prefix, String key, String text){
        String answer = text;
        if(text.contains("{{")){
            String extracted_text = text.substring(text.indexOf("{{"), text.indexOf("}}")+ 2);
            String replacingText = extracted_text.replace("{{", "");
            replacingText = "{{"+ prefix + "." +replacingText ;
            answer = text.replace(extracted_text, replacingText);
        }
        return answer;
    }


}
