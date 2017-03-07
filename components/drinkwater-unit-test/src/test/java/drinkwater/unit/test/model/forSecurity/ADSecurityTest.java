package drinkwater.unit.test.model.forSecurity;

import drinkwater.ApplicationBuilder;
import drinkwater.core.security.ActiveDirectoryAuthenticationService;
import drinkwater.security.IAuthenticationService;

public class ADSecurityTest extends ApplicationBuilder {

    @Override
    public void configure() {
        try {

                      addService("authenticator",
                              IAuthenticationService.class,
                              ActiveDirectoryAuthenticationService.class);
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }

}
