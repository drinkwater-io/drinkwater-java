package drinkwater.examples.numberservice;



import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class NumberServiceTest
{

    @Test
    public void shouldPassTheHappyPath() throws Exception {
        long startTime = System.nanoTime();

        AccountService accountservice = new AccountService();
        NumberService numberService = new NumberService();
        numberService.setNumberFormatter(new NumberFormatter());
        numberService.setNumberRepository(new NumberFileRepository("c:/temp"));
        numberService.setAccountService(accountservice);

        //create an account
        Account acc = accountservice.createAccount("cedric", "secret");
        accountservice.login("cedric", "secret");

        numberService.saveNumber(acc, 10);
        numberService.saveNumber(acc, 20);
        numberService.saveNumber(acc, 30);

        List<String> numbers = numberService.getNumberList(acc);

        assertEquals(3, numbers.size());

        numberService.clear(acc);

        long estimatedTime = System.nanoTime() - startTime;

        System.out.println("test took " + TimeUnit.NANOSECONDS.toMillis(estimatedTime) + " millis");
    }

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
