package next.api;

/**
 * Created by A406775 on 24/03/2017.
 */
public interface IInjectorAdapter {

    default void start(){}

    default void stop(){}

    <T> T get(Class<? extends T> clazz);
}
