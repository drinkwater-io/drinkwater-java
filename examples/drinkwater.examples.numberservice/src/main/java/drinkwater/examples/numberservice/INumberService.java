package drinkwater.examples.numberservice;

import java.util.List;

/**
 * Created by A406775 on 28/12/2016.
 */
public interface INumberService {

    String saveNumber(Account account, int number) throws Exception;

    List<String> getNumberList(Account account) throws Exception;

    void clear(Account account) throws Exception;
}
