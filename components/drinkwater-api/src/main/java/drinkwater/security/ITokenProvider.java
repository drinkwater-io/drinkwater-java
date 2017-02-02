package drinkwater.security;

public interface ITokenProvider {
    String createToken(Credentials credentials) throws Exception;
}
