package drinkwater.core.security;

import drinkwater.IPropertyResolver;
import drinkwater.helper.GeneralUtils;
import drinkwater.security.IAuthenticationService;
import drinkwater.security.ITokenValidation;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.util.logging.Logger;

public class SimpleTokenValidation implements ITokenValidation {

    private Logger logger = Logger.getLogger(SimpleTokenValidation.class.getName());

    private StandardPBEStringEncryptor textEncryptor;

    private String encryptionSecret;

    public SimpleTokenValidation(String encryptionSecret) {
        this.encryptionSecret = encryptionSecret;
    }

    public StandardPBEStringEncryptor getTextEncryptor(){
        if(textEncryptor == null) {
            textEncryptor = new StandardPBEStringEncryptor();
            textEncryptor.setPassword(encryptionSecret);
        }
        return textEncryptor;
    }

    @Override
    public boolean isTokenvalid(String serializedToken) {
        try {

            String decryptedToken = getTextEncryptor().decrypt(serializedToken);

            SimpleToken token = GeneralUtils.fromJsonString(decryptedToken, SimpleToken.class);

            return token.isValid();
        }
        catch(Exception ex){
            logger.severe("could not decrypt given token : " + serializedToken);
            return false;
        }
    }
}
