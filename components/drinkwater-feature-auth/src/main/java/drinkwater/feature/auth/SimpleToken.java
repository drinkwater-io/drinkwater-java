package drinkwater.support.tokenprovider;


import drinkwater.helper.GeneralUtils;
import drinkwater.security.ISecurityToken;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

public class SimpleToken implements ISecurityToken {
    public  String id;
    public Instant creationdate;
    public long validity;
    public Map<String,String> claims;

    public SimpleToken() {
        this.creationdate = Instant.now();
        this.id = UUID.randomUUID().toString();
    }

    public static SimpleToken of(long validity,  Map<String,String> claims){
        SimpleToken token = new SimpleToken();
        token.validity = validity;
        token.claims = claims;
        return token;
    }

    public boolean isValid() {
        long between = ChronoUnit.MILLIS.between(creationdate, Instant.now());
        if ((validity - between) < 0) {
            return false;
        }
        return true;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public Instant getCreationdate() {
        return creationdate;
    }

    @Override
    public long getValidity() {
        return validity;
    }

    @Override
    public Map<String, String> getClaims() {
        return claims;
    }

    public static String createFakeToken(String password, long validity,  Map<String, String> properties){
        try {
            StandardPBEStringEncryptor textEncryptor = new StandardPBEStringEncryptor();
            textEncryptor.setPassword(password);

            SimpleToken token = SimpleToken.of(validity, properties);

            String result = GeneralUtils.toJsonString(token);
            String encryptedResult = textEncryptor.encrypt(result);

            return encryptedResult;
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}