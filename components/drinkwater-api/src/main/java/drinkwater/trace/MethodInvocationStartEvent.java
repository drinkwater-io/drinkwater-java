package drinkwater.trace;

import java.time.Instant;

/**
 * Created by A406775 on 6/01/2017.
 */
public class MethodInvocationStartEvent extends BaseEvent {
    public MethodInvocationStartEvent(Instant instant, String correlationId, String description, String applicationName, String serviceName,Payload payloads) {
        super(instant, "MIS", correlationId, description, applicationName,serviceName, payloads);
    }
}
