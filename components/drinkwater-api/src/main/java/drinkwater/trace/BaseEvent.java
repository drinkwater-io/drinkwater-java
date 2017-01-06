package drinkwater.trace;

import java.time.Instant;

/**
 * Created by A406775 on 5/01/2017.
 */
public class BaseEvent {

    private Instant time;

    private Payload payload;

    private String name;

    private String description;

    private String correlationId;

    public BaseEvent(Instant instant, String name, String correlationId, String description, Payload payload) {
        this.time = instant;
        this.description = description;
        this.name = name;
        this.payload = payload;
        this.correlationId = correlationId;
    }

    public String getDescription() {
        return description;
    }

    public Instant getTime() {
        return time;
    }

    public Payload getPayload() {
        return payload;
    }

    public String getName() {
        return name;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public String toString() {
        return time + "---" + correlationId + "---" + name + "---" + description;
    }
}
