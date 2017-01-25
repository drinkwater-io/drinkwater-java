package drinkwater.trace;

import java.time.Instant;

/**
 * Created by A406775 on 6/01/2017.
 */
public class MethodInvocationEndEvent extends BaseEvent {
    public MethodInvocationEndEvent(Instant instant, String correlationId, String description,
                                    String applicationName, String serviceName,Payload payloads) {
        super(instant, "MIE", correlationId, description, applicationName, serviceName,payloads);
    }
}
