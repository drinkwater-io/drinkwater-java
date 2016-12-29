package drinkwater.examples.drinktracker.model;

/**
 * Created by A406775 on 27/12/2016.
 */
public class Account {

    private String acountId;

    private String accountName;

    private String accountPassword;

    private boolean isAuthenticated;

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
