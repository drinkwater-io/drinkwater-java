package drinkwater.security;

import drinkwater.security.Credentials;

import java.util.Map;

public interface IAuthenticationService {
    Map<String,String> authenticate(Credentials credentials) throws Exception;
}
