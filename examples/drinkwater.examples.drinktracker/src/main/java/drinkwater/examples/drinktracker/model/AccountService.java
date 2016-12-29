package drinkwater.examples.drinktracker.model;

import javaslang.collection.List;

import java.util.UUID;

/**
 * Created by A406775 on 27/12/2016.
 */
public class AccountService implements IAccountService {

    private static List<Account> accounts ;

    public AccountService(){
        accounts = List.empty();
    }

    @Override
    public Account createAccount(String name, String password) throws Exception {

        List<Account> existing = accounts.filter(a -> a.getAccountName().equals(name)).toList();

        if(existing.size() > 0){
            throw new Exception("account already exists");
        }

        Account acc = new Account();
        acc.setAccountName(name);
        acc.setAccountPassword(password);
        acc.setAcountId(UUID.randomUUID().toString());
        accounts = accounts.append(acc);
        return acc;
    }

    @Override
    public Account get(String id) {
        return accounts.filter(a -> a.getAcountId().equals(id)).get();
    }

    @Override
    public Account login(String name, String password) throws Exception {
        List<Account> acclist = accounts.filter(a -> a.getAccountName().equals(name))
                .filter(a -> a.getAccountPassword().equals(password)).toList();

        if (acclist.size() == 0) {
            throw new Exception("login failed");
        }

        acclist.get().setAuthenticated(true);

        return acclist.get();
    }

    @Override
    public void logoff(Account acc) throws Exception {
        acc.setAuthenticated(false);
    }

    public boolean isAuthenticated(Account acc){
        return acc.isAuthenticated();
    }

    public java.util.List<Account> getAll()
    {
        return accounts.toJavaList();
    }

    @Override
    public void clear() {
        accounts = List.empty();
    }
}
