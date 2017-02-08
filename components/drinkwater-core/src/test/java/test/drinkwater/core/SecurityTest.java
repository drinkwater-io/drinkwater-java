package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.security.SimpleToken;
import drinkwater.helper.GeneralUtils;
import drinkwater.security.Credentials;
import drinkwater.test.HttpUnitTest;
import drinkwater.trace.ConsoleEventLogger;
import org.junit.Test;
import test.drinkwater.core.model.forSecurity.ADSecurityTest;
import test.drinkwater.core.model.forSecurity.SecurityTestConfiguration;
import test.drinkwater.core.model.forSecurity.TokenProviderConfiguration;

import java.util.HashMap;
import java.util.Map;

import static drinkwater.ApplicationOptionsBuilder.options;
import static org.assertj.core.api.Assertions.assertThat;

public class SecurityTest extends HttpUnitTest {

    @Test
    public void shouldCheckRequestAuthorization() throws Exception {
        try (DrinkWaterApplication app =
                     DrinkWaterApplication.create("security-test",
                             options()
                                     .useTracing()
                                     .use(SecurityTestConfiguration.class)
                                     .use(ConsoleEventLogger.class)
                                     .autoStart())) {
            String result = "";

            //test simple service
            result = httpGetString("http://localhost:8889/test/info").result();
            assertThat(result).isEqualTo("test info");

            result = httpGetString("http://localhost:8889/secured/info")
                    .expectsStatus(401).result();
            assertThat(result).isEqualTo("Unauthorized");

            //test secure service
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "TOKEN " + createNewToken(360000));
            result = httpGetString("http://localhost:8889/secured/info", headers).expectsStatus(200).result();
            assertThat(result).isEqualTo("test info");


            headers = new HashMap<>();
            headers.put("Authorization", "TOKEN " + createNewToken(100));
            Thread.sleep(101);
            result = httpGetString("http://localhost:8889/secured/info", headers).expectsStatus(401).result();
            assertThat(result).isEqualTo("Unauthorized");

        }
    }

    @Test
    public void shouldCreateNewToken() throws Exception {
        try (DrinkWaterApplication app =
                     DrinkWaterApplication.create("security-test",
                             options()
                                     .useTracing()
                                     .use(TokenProviderConfiguration.class)
                                     .use(ConsoleEventLogger.class)
                                     .autoStart())) {
            String result = "";

            Credentials credentials = new Credentials("cedric","Dumont");
            String body = GeneralUtils.toJsonString(credentials);

            result = httpPostRequestString("http://localhost:8889/auth/token", body)
                    .expectsStatus(200)
                    .result();

            credentials = new Credentials("unknown","unknown");
            body = GeneralUtils.toJsonString(credentials);

            result = httpPostRequestString("http://localhost:8889/auth/token", body)
                    .expectsStatus(401)
                    .result();

            assertThat(result).isEqualTo("Unauthorized");
        }
    }

    public String createNewToken(long validity) throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("sub", "goldorak");
        properties.put("mail", "goldorak@vega.universe");
        properties.put("roles", "HEARTH_PROTECTOR,GLOGOTH_KILLER");

        return  SimpleToken.createFakeToken("secret", validity, properties);
    }

   // @Test
    public void shouldCreateNewTokenFromAD() throws Exception {
        try (DrinkWaterApplication app =
                     DrinkWaterApplication.create("security-ad-test",
                             options()
                                     .useTracing()
                                     .use(ADSecurityTest.class)
                                     .use(ConsoleEventLogger.class)
                                     .autoStart())) {
            String result = "";

            Credentials credentials = new Credentials("xxx","xx");
            String body = GeneralUtils.toJsonString(credentials);

            result = httpPostRequestString("http://localhost:8889/auth/token", body)
                    .expectsStatus(200)
                    .result();

            assertThat(result).isNotNull();
        }
    }
}
