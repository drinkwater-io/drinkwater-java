package drinkwater.trace;

import java.time.Instant;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ClientReceivedEvent extends BaseEvent {

    public ClientReceivedEvent(Instant instant, String correlationId, String description,
                               String applicationName, String serviceName,Payload payloads) {
        super(instant, "CLR", correlationId, description, applicationName, serviceName, payloads);
    }
}
