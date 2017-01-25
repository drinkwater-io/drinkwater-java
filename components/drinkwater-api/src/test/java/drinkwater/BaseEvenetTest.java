package drinkwater;

import drinkwater.trace.ClientReceivedEvent;
import drinkwater.trace.Payload;
import org.junit.Test;

import java.time.Instant;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;


/**
 * Unit test for simple App.
 */
public class BaseEvenetTest {

    @Test
    public void testCreateEvent() {
        ClientReceivedEvent clientReceivedEvent = new ClientReceivedEvent(
                Instant.now(), "1", "description", "testApp", "testservice",
                Payload.of(null, new HashMap<String, Object>(), "someBody"));

        Payload payload = clientReceivedEvent.getPayload();

        assertEquals(null, payload.getOperation());
        assertEquals("someBody", payload.getBody());
    }

}
