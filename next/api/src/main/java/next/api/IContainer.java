package next.api;

/**
 * Created by A406775 on 24/03/2017.
 */
public interface IContainer {
    <T> T get(Class<? extends T> clazz);
}
