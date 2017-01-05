package drinkwater.trace;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ClientReceivedEvent extends BaseEvent {

    public ClientReceivedEvent(String correlationId, String description, Object... payloads) {
        super(correlationId, "ClientReceivedEvent", description, payloads);
    }
}
