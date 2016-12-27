package drinkwater.examples.numberservice;

import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public interface INumberRepository {
    void registerSomeInfo(String filePath, String info);

    List<String> getNumbers(String filePath);

    void clear(String filePath);
}
