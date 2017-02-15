package test.drinkwater.core;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.security.SimpleToken;
import drinkwater.helper.GeneralUtils;
import drinkwater.rest.RestService;
import drinkwater.security.Credentials;
import drinkwater.test.HttpUnitTest;
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
                                     .use(SecurityTestConfiguration.class)
                                     .autoStart())) {
            String result = "";

            //test simple service

            String testPort = (String)app.getServiceProperty("test", RestService.REST_PORT_KEY);
            String securedPort = (String)app.getServiceProperty("secured", RestService.REST_PORT_KEY);

            result = httpGetString(String.format("http://localhost:%s/test/info",testPort)).result();
            assertThat(result).isEqualTo("test info");

            result = httpGetString(String.format("http://localhost:%s/secured/info", securedPort))
                    .expectsStatus(401).result();
            assertThat(result).isEqualTo("Unauthorized");

            //test secure service
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "TOKEN " + createNewToken(360000));
            result = httpGetString(String.format("http://localhost:%s/secured/info", securedPort), headers).expectsStatus(200).result();
            assertThat(result).isEqualTo("test info");


            headers = new HashMap<>();
            headers.put("Authorization", "TOKEN " + createNewToken(100));
            Thread.sleep(101);
            result = httpGetString(String.format("http://localhost:%s/secured/info", securedPort), headers).expectsStatus(401).result();
            assertThat(result).isEqualTo("Unauthorized");

        }
    }

    @Test
    public void shouldCreateNewToken() throws Exception {
        try (DrinkWaterApplication app =
                     DrinkWaterApplication.create("security-test",
                             options()
                                     .use(TokenProviderConfiguration.class)
                                     .autoStart())) {
            String result = "";

            Credentials credentials = new Credentials("cedric","Dumont");
            String body = GeneralUtils.toJsonString(credentials);

            String authPort = app.getTokenServicePort();

            result = httpPostRequestString(String.format("http://localhost:%s/auth/token", authPort), body)
                    .expectsStatus(200)
                    .result();

            credentials = new Credentials("unknown","unknown");
            body = GeneralUtils.toJsonString(credentials);

            result = httpPostRequestString(String.format("http://localhost:%s/auth/token", authPort), body)
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
                                     .use(ADSecurityTest.class)
                                     .autoStart())) {
            String result = "";

            Credentials credentials = new Credentials("xxx","xx");
            String body = GeneralUtils.toJsonString(credentials);

            String authPort = (String)app.getServiceProperty("auth", RestService.REST_PORT_KEY);

            result = httpPostRequestString(String.format("http://localhost:%s/auth/token", authPort), body)
                    .expectsStatus(200)
                    .result();

            assertThat(result).isNotNull();
        }
    }
}
