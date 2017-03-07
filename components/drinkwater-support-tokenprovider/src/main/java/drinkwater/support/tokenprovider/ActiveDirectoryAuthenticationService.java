package drinkwater.support.tokenprovider;

import com.sun.jndi.ldap.LdapCtxFactory;
import drinkwater.security.Credentials;
import drinkwater.security.IAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.*;
import java.util.stream.Collectors;

import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;

public class ActiveDirectoryAuthenticationService implements IAuthenticationService {

    private static Logger logger = LoggerFactory.getLogger(ActiveDirectoryAuthenticationService.class);

    public String domainName;

    public String serverName;

    @Override
    public Map<String, String> authenticate(Credentials credentials) throws Exception {

        Hashtable props = new Hashtable();
        String principalName = credentials.getUser() + "@" + domainName;
        props.put(Context.SECURITY_PRINCIPAL, principalName);
        props.put(Context.SECURITY_CREDENTIALS, credentials.getPassword());
        DirContext context;

        try {
            //context = LdapCtxFactory.getLdapCtxInstance("ldap://" + serverName + "." + domainName + '/', props);
            context = LdapCtxFactory.getLdapCtxInstance("ldap://" + serverName + '/', props);
            System.out.println("Authentication succeeded!");

            // locate this user's record
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> renum = context.search(toDC(domainName),
                    "(& (userPrincipalName=" + principalName + ")(objectClass=user))", controls);
            if (!renum.hasMore()) {
                throw new Exception("Cannot locate user information for " + credentials.getUser());
            }
            SearchResult result = renum.next();

            List<String> groups = new ArrayList<String>();
            Attribute memberOf = result.getAttributes().get("memberOf");
            if (memberOf != null) {// null if this user belongs to no group at all
                for (int i = 0; i < memberOf.size(); i++) {
                    Attributes atts = context.getAttributes(memberOf.get(i).toString(), new String[]{"CN"});
                    Attribute att = atts.get("CN");
                    groups.add(att.get().toString());
                }
            }

            context.close();

            String roles = groups.stream().collect(Collectors.joining(","));

            Map<String, String> claims = new HashMap<>();
            claims.put("sub", credentials.getUser());
            claims.put("name", credentials.getUser());
            claims.put("roles", roles);

            return claims;


        } catch (Exception a) {

            logger.error("authentication exception " + a.getMessage());
            return null;
        }

    }


    private static String toDC(String domainName) {
        StringBuilder buf = new StringBuilder();
        for (String token : domainName.split("\\.")) {
            if (token.length() == 0)
                continue; // defensive check
            if (buf.length() > 0)
                buf.append(",");
            buf.append("DC=").append(token);
        }
        return buf.toString();
    }


}
