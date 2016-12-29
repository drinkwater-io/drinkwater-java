package drinkwater.core.reflect;

import java.lang.reflect.InvocationHandler;
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
}
