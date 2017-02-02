package drinkwater.security;

public class Credentials {
    private String user;

    private String password;

    public Credentials(){}

    public Credentials(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        Credentials equalsTo = (Credentials)obj;
        boolean sameUser = equalsTo.getUser().equals(user);
        boolean samePassword = equalsTo.getPassword().equals(password);

        return sameUser && samePassword;
    }
}
