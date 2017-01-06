package drinkwater;

import drinkwater.trace.ClientReceivedEvent;
import drinkwater.trace.Payload;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;


/**
 * Unit test for simple App.
 */
public class BaseEvenetTest {

    @Test
    public void testCreateEvent() {
        ClientReceivedEvent clientReceivedEvent = new ClientReceivedEvent(Instant.now(), "1", "description", Payload.of("test", "test2"));

        Payload payload = clientReceivedEvent.getPayload();

        assertEquals("test", payload.getTarget()[0]);
        assertEquals("test2", payload.getTarget()[1]);
    }

}
