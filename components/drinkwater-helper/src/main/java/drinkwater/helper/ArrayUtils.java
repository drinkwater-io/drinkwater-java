package drinkwater.helper;

public class ArrayUtils {

    public static <T> T[] addAll(T[] left, T[] right) {
        return org.apache.commons.lang3.ArrayUtils.addAll(
                left,
                right);
    }
}
