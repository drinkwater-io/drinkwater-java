package drinkwater.security;

import java.time.Instant;
import java.util.Map;

public interface ISecurityToken {

    String getId();

    Instant getCreationdate();

    long getValidity();

    Map<String, String> getClaims();
}
