package drinkwater.examples.drinktracker.test;



import drinkwater.examples.drinktracker.model.Account;
import drinkwater.examples.drinktracker.model.AccountService;
import drinkwater.examples.drinktracker.model.IAccountService;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class NumberServiceTest
{

    @Test
    public void shouldCreateAccounts() throws Exception {
        IAccountService accountservice = new AccountService();

        //create an account
        Account acc = accountservice.createAccount("cedric", "secret");
        Account acc2 = accountservice.createAccount("albert", "secret2");

        List<Account> accounts = accountservice.getAll();

        assertEquals(2, accounts.size());

    }

    @Test
    public void shouldNotCreateAccountsTwice() throws Exception {
        IAccountService accountservice = new AccountService();

        try {
            //create an account
            Account acc = accountservice.createAccount("cedric", "secret");
            Account acc2 = accountservice.createAccount("cedric", "secret2");
            assertTrue(false);
        }catch(Exception ex){
        }

        List<Account> accounts = accountservice.getAll();

        assertEquals(1, accounts.size());

    }
}
