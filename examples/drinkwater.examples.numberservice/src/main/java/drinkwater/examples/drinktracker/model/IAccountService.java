package drinkwater.examples.drinktracker.model;

import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public interface IAccountService {
    Account createAccount(String name, String password) throws Exception;

    Account get(String id);

    Account login(String name, String password) throws Exception;

    void logoff(Account acc) throws Exception;

    boolean isAuthenticated(Account acc);

    List<Account> getAll();

    void clear();
}
