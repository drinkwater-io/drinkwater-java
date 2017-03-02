package drinkwater.feature.auth;

import drinkwater.security.ITokenValidation;
import drinkwater.security.UnauthorizedException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Set;

public class SecurityProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String header = (String) exchange.getIn().getHeader("Authorization");

        if (header == null) {
            throw new UnauthorizedException();
        }

        Set<ITokenValidation> providers =
                exchange.getContext().getRegistry().findByType(ITokenValidation.class);

        if (providers.isEmpty()) {
            throw new UnauthorizedException();
        }

        ITokenValidation tokenProvider = providers.iterator().next();

        String token = header.replace("TOKEN", "");
        token = token.trim();
        boolean isvalid = tokenProvider.isTokenvalid(token);

        if (!isvalid) {
            throw new UnauthorizedException();
        }
    }
}
