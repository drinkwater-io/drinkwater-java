package drinkwater.trace;

import drinkwater.IBaseEventLogger;

import java.lang.reflect.Method;

/**
 * Created by A406775 on 6/01/2017.
 */
public class ConsoleEventLogger implements IBaseEventLogger {
    @Override
    public void logEvent(BaseEvent event) {
        try {
            Method m = (Method) event.getPayload().getTarget()[0];
            if (m.getName().equals("toString")) {
                return;
            }
        } catch (Exception e) {
        }

        System.out.println(event.getName() + " : " + event.getCorrelationId() + " -> " + event.getTime() + "  -  " + event.getDescription());
    }

}
