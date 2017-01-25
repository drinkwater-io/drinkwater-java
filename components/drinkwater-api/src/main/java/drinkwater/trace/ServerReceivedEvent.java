package drinkwater.trace;

import java.time.Instant;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServerReceivedEvent extends BaseEvent {
    public ServerReceivedEvent(Instant instant, String correlationId, String description,
                               String applicationName, String serviceName,Payload payloads) {
        super(instant, "SER", correlationId, description, applicationName, serviceName, payloads);
    }
}
