package drinkwater.trace;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServerReceivedEvent extends BaseEvent {
    public ServerReceivedEvent(String correlationId, String description, Object... payloads) {
        super(correlationId, "ServerReceivedEvent", description, payloads);
    }
}
