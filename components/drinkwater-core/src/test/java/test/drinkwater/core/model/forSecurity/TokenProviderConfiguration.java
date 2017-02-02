package test.drinkwater.core.model.forSecurity;

import drinkwater.ApplicationBuilder;
import drinkwater.security.Credentials;
import drinkwater.security.IAuthenticationService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import test.drinkwater.core.model.ITestService;
import test.drinkwater.core.model.TestServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by A406775 on 4/01/2017.
 */
public class TokenProviderConfiguration extends ApplicationBuilder {
    @Override
    public void configure() {
        try {

            IAuthenticationService authService = mock(IAuthenticationService.class);
            //for anyone return some claims
            when(authService.authenticate(any())).then(getSimpleClaims());
            //for unknown return null;
            when(authService.authenticate(new Credentials("unknown", "unknown")))
                    .thenReturn(null);

            addService("authenticator", IAuthenticationService.class, authService).useTracing(true);
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }

    public static Answer getSimpleClaims() throws IOException {
        return new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                try {
                    Map<String, String> properties = new HashMap<>();
                    properties.put("sub", "goldorak");
                    properties.put("mail", "goldorak@vega.universe");
                    properties.put("roles", "HEARTH_PROTECTOR,GLOGOTH_KILLER");
                    return properties;

                }
                catch(Exception ex){
                    ex.printStackTrace();
                    throw ex;
                }
            }
        };

    }
}


