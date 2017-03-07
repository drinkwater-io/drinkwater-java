package drinkwater.support.tokenprovider;

import drinkwater.ServiceDependency;
import drinkwater.feature.auth.SimpleToken;
import drinkwater.helper.GeneralUtils;
import drinkwater.security.Credentials;
import drinkwater.security.IAuthenticationService;
import drinkwater.security.ITokenProvider;
import drinkwater.security.UnauthorizedException;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SimpleTokenProvider implements ITokenProvider {

    private static Logger logger = LoggerFactory.getLogger(SimpleTokenProvider.class);

    private StandardPBEStringEncryptor textEncryptor;

    @ServiceDependency
    public IAuthenticationService authService;

    //should be injected by the framework
    public String encryptionSecret;

    //should be injected by the framework
    public Long tokenLifeTimeMillis;

    private StandardPBEStringEncryptor getTextEncryptor(){
        if(textEncryptor == null) {
            textEncryptor = new StandardPBEStringEncryptor();
            textEncryptor.setPassword(encryptionSecret);
        }
        return textEncryptor;
    }

    @Override
    public String createToken(Credentials credentials) throws Exception{

        try {

            Map<String, String> result = authService.authenticate(credentials);

            if (result == null) {
                throw new UnauthorizedException();
            }

            SimpleToken token = SimpleToken.of(tokenLifeTimeMillis, result);

            String serializedToken = GeneralUtils.toJsonString(token);

            String encryptedToken = getTextEncryptor().encrypt(serializedToken);

            return encryptedToken;
        }
        catch(Exception ex){
            logger.error("UnauthorizedException throwed : " + ex.getMessage());
            throw new UnauthorizedException(ex);
        }
    }

}



