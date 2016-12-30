package drinkwater.helper.reflect;

import javaslang.collection.List;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.stream.Stream;

/**
 * Created by A406775 on 29/12/2016.
 */
public final class ReflectHelper {
    public static <T> T simpleProxy(Class<? extends T> iface, InvocationHandler handler, Class<?>... otherIfaces) {
        Class<?>[] allInterfaces = Stream.concat(
                Stream.of(iface),
                Stream.of(otherIfaces))
                .distinct()
                .toArray(Class<?>[]::new);

        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                allInterfaces,
                handler);
    }

    public static Method[] getPublicDeclaredMethods(Class clazz) {
        return List.of(clazz.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .toJavaArray(Method.class);
    }

    public static boolean returnsVoid(Method method) {
        return method.getReturnType().equals(Void.TYPE);
    }

    public static boolean isPrimitive(Class clazz) {
        boolean isPrimitiveOrWrapped =
                clazz.isPrimitive() || ClassUtils.wrapperToPrimitive(clazz) != null;

        return isPrimitiveOrWrapped;
    }

    public static boolean isString(Class clazz) {
        if (clazz.equals(String.class)) {
            return true;
        }
        return false;
    }

    public static boolean isPrimitiveOrString(Class clazz) {
        return isString(clazz) || isPrimitive(clazz);
    }

}
