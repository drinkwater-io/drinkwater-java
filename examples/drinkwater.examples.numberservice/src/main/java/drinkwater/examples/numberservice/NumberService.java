package drinkwater.examples.numberservice;

import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public class NumberService {

    private INumberRepository numberRepository;

    private INumberFormatter numberFormatter;

    private IAccountService accountService;

    public NumberService() {
    }

    public NumberService(
            IAccountService accountService,
            INumberRepository numberRepository,
            INumberFormatter numberFormatter) {
        this.accountService = accountService;
        this.numberRepository = numberRepository;
        this.numberFormatter = numberFormatter;
    }

    public INumberRepository getNumberRepository() {
        return numberRepository;
    }

    public void setNumberRepository(INumberRepository numberRepository) {
        this.numberRepository = numberRepository;
    }

    public INumberFormatter getNumberFormatter() {
        return numberFormatter;
    }

    public void setNumberFormatter(INumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    public IAccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    //API

    public String saveNumber(Account account, int number) throws Exception {

        checkAuthenticated(account);

        //convert number to string
        String numberAsString = Integer.toString(number);

        //check that length is 10
        while (numberAsString.length() < 5) {
            numberAsString = numberFormatter.prependZero(numberAsString);
        }

        //register the info
        numberRepository.saveNumber(account, numberAsString);

        return numberAsString;
    }

    public List<String> getNumberList(Account account) throws Exception {
        checkAuthenticated(account);

        return numberRepository.getNumbers(account);
    }

    public void clear(Account account) throws Exception {

        checkAuthenticated(account);

        numberRepository.clearNumbers(account);
    }

    private void checkAuthenticated(Account account) throws Exception {
        if (!accountService.isAuthenticated(account)) {
            throw new Exception("should authenticate first");
        }
    }

}
