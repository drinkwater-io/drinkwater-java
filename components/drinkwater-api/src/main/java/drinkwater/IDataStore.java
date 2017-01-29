package drinkwater;

import java.io.Closeable;

/**
 * Created by A406775 on 11/01/2017.
 */
public interface IDataStore extends Closeable {

    void migrate();

    void start() throws  Exception;


}
