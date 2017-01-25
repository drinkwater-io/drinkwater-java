package drinkwater.trace;

import java.time.Instant;

public class ExceptionEvent extends BaseEvent{
    public ExceptionEvent(Instant instant, String correlationId, String description, String applicationName, String serviceName,Payload payloads) {
        super(instant, "EXC", correlationId, description,
                applicationName, serviceName,payloads);
    }

}

