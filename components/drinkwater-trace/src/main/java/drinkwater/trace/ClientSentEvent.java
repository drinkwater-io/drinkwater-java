package drinkwater.trace;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ClientSentEvent extends BaseEvent {
    public ClientSentEvent(String correlationId, String description, Payload payload) {
        super(correlationId, "ClientSentEvent", description, payload);
    }
}
