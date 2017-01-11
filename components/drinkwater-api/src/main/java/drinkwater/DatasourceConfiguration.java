package drinkwater;

/**
 * Created by A406775 on 11/01/2017.
 */
public class DatasourceConfiguration {
    private String url;

    private String driverClassname;

    private String user;

    private String password;

    public DatasourceConfiguration(String url, String driverClassname, String user, String password) {
        this.url = url;
        this.driverClassname = driverClassname;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassname() {
        return driverClassname;
    }

    public void setDriverClassname(String driverClassname) {
        this.driverClassname = driverClassname;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
