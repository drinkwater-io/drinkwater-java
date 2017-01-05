package drinkwater.trace;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServerSentEvent extends BaseEvent {
    public ServerSentEvent(String correlationId, String description, Payload payloads) {
        super(correlationId, "ServerSentEvent", description, payloads);
    }
}
