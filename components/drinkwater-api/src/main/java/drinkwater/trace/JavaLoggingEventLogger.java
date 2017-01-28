package drinkwater.trace;

import drinkwater.IBaseEventLogger;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by A406775 on 6/01/2017.
 */
public class JavaLoggingEventLogger implements IBaseEventLogger {

    private Logger logger = Logger.getLogger(JavaLoggingEventLogger.class.getName());

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
