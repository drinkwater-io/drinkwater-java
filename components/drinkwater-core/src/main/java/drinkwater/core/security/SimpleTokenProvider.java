package drinkwater.core.security;

import drinkwater.ServiceDependency;
import drinkwater.helper.GeneralUtils;
import drinkwater.rest.Path;
import drinkwater.security.Credentials;
import drinkwater.security.IAuthenticationService;
import drinkwater.security.ITokenProvider;
import drinkwater.security.UnauthorizedException;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.util.Map;
import java.util.logging.Logger;

public class SimpleTokenProvider implements ITokenProvider {

    private Logger logger = Logger.getLogger(SimpleTokenProvider.class.getName());

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
            logger.severe("UnauthorizedException throwed : " + ex.getMessage());
            throw new UnauthorizedException(ex);
        }
    }

}



