package drinkwater;

import drinkwater.trace.ClientReceivedEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testCreateEvent() {
        ClientReceivedEvent event = new ClientReceivedEvent("test", "test2");

        Object[] test = event.getPayloads();

        assertEquals("test", test[0]);
        assertEquals("test2", test[1]);
    }

}
