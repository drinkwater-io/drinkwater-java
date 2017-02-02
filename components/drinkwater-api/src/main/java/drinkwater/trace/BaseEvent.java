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

    private String applicationName;

    private String serviceName;

    public BaseEvent(Instant instant, String name, String correlationId, String description,
                     String applicationName, String serviceName,Payload payload) {
        this.time = instant;
        this.description = description;
        this.name = name;
        this.payload = payload;
        this.correlationId = correlationId;
        this.serviceName = serviceName;
        this.applicationName = applicationName;
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

    public String getApplicationName() {
        return applicationName;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {

        return time + " - " + correlationId + " | " + applicationName + "." + serviceName + "\t - " + name + " | " + description;
    }
}
