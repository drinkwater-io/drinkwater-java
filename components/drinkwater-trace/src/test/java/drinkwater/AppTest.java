package drinkwater;

import drinkwater.trace.ClientReceivedEvent;
import drinkwater.trace.Payload;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testCreateEvent() {
        ClientReceivedEvent clientReceivedEvent = new ClientReceivedEvent("1", "desc", Payload.of("test", "test2"));

        Payload payload = clientReceivedEvent.getPayload();

        assertEquals("test", payload.getTarget()[0]);
        assertEquals("test2", payload.getTarget()[1]);
    }

}
