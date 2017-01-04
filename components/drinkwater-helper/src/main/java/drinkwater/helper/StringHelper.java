package drinkwater.helper;

import javaslang.collection.List;

import java.util.Arrays;

/**
 * Created by A406775 on 30/12/2016.
 */
public final class StringHelper {

    public static boolean startsWithOneOf(String value, String[] prefixes) {
        boolean result = List.of(prefixes)
                .filter(p -> value.toLowerCase().startsWith(p))
                .length() > 0;

        return result;
    }

    public static boolean oneOf(String value, String[] values) {
        return oneOf(value, Arrays.asList(values));
    }

    public static boolean oneOf(String value, java.util.List<String> values) {
        boolean result = List.ofAll(values)
                .filter(p -> value.equals(p))
                .length() > 0;

        return result;
    }

    public static String trimEnclosingQuotes(String tobetrimmed) {
        if (tobetrimmed == null) {
            return null;
        }
        return tobetrimmed.replaceAll("^\"|\"$", "");
    }
}
