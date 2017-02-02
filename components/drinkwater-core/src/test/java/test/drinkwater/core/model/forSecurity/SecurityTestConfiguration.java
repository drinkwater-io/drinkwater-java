package test.drinkwater.core.model.forSecurity;

import drinkwater.ApplicationBuilder;
import drinkwater.helper.GeneralUtils;
import drinkwater.security.IAuthenticationService;
import drinkwater.security.ITokenProvider;
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
public class SecurityTestConfiguration extends ApplicationBuilder {
    @Override
    public void configure() {
        try {
            addService("test", ITestService.class, new TestServiceImpl())
                    .useTracing(true)
                    .asRest();

            addService("secured", ITestService.class, new TestServiceImpl())
                    .useTracing(true)
                    .asRest();
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }
}

