package drinkwater.core;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface ServiceRepository {
    <T> T getService(Class<? extends T> iface);
}
