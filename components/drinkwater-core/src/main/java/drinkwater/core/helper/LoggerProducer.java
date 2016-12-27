package drinkwater.core.helper;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Helper to save some boilerplate
 */
public class LoggerProducer {

    @Produces
    public Logger produceLogger(InjectionPoint ip){
        return Logger.getLogger(ip.getMember().getDeclaringClass().getName());
    }
}
