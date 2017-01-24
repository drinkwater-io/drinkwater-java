package drinkwater.trace;

import java.time.Instant;

public class ExceptionEvent extends BaseEvent{
    public ExceptionEvent(Instant instant, String correlationId, String description, Payload payloads) {
        super(instant, "EXC", correlationId, description, payloads);
    }

}

