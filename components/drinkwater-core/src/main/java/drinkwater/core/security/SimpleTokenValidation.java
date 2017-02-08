package drinkwater.core.security;

import drinkwater.helper.GeneralUtils;
import drinkwater.security.ITokenValidation;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTokenValidation implements ITokenValidation {

    private static Logger logger = LoggerFactory.getLogger(SimpleTokenValidation.class);

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
            logger.error("could not decrypt given token : " + serializedToken);
            return false;
        }
    }
}
