package drinkwater.trace;

import drinkwater.IBaseEventLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by A406775 on 6/01/2017.
 */
public class JavaLoggingEventLogger implements IBaseEventLogger {

    private static Logger logger = LoggerFactory.getLogger(JavaLoggingEventLogger.class);

    @Override
    public void logEvent(BaseEvent event) {
        try {
            Method m = (Method) event.getPayload().getTarget()[0];
            if (m.getName().equals("toString")) {
                return;
            }
        } catch (Exception e) {
        }

        logger.info(event.getTime() + " - " + event.getCorrelationId() + " - "
                + event.getApplicationName() + "." + event.getServiceName() + " - " + event.getName() + " : "
                + event.getDescription());
    }

}
