package examples.drinkwater.drinktracker.model;

/**
 * Created by A406775 on 27/12/2016.
 */
public class Account {

    private String acountId;

    private String accountName;

    private String accountPassword;

    private boolean isAuthenticated;

    public static Account from(String accountId, String accountName, String accountPassword, boolean isAuthenticated) {
        Account acc = new Account();
        acc.accountName = accountName;
        acc.isAuthenticated = isAuthenticated;
        acc.accountPassword = accountPassword;
        acc.acountId = accountId;
        return acc;
    }

    public String getAcountId() {
        return acountId;
    }

    public void setAcountId(String acountId) {
        this.acountId = acountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }
}
