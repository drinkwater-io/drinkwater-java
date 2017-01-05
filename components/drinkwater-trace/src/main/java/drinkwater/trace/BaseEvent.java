package drinkwater.trace;

import java.time.LocalDateTime;

/**
 * Created by A406775 on 5/01/2017.
 */
public class BaseEvent {

    private LocalDateTime time;

    private Payload payload;

    private String name;

    private String description;

    private String correlationId;

    public BaseEvent(String correlationId, String name, String description, Payload payload) {
        //check new time java api with clock ?
        this.time = LocalDateTime.now();
        this.description = description;
        this.name = name;
        this.payload = payload;
        this.correlationId = correlationId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTime() {
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
        return correlationId + " - " + name + " - " + description + " - " + time;
    }
}
