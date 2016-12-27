package drinkwater.examples.numberservice;

import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public interface INumberRepository {

    void saveNumber(Account account, String info);

    List<String> getNumbers(Account account);

    void clearNumbers(Account account);
}
