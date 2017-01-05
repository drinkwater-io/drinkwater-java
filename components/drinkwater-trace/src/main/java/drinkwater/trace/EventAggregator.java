package drinkwater.trace;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by A406775 on 5/01/2017.
 */
public class EventAggregator {

    public Queue<BaseEvent> events = new ConcurrentLinkedQueue<>();

    public void addEvent(BaseEvent event) {
        events.add(event);
    }

    public BaseEvent getLast(BaseEvent event) {
        return events.poll();
    }

    public int currentSize() {
        return events.size();
    }

    public void clear() {
        events.clear();
    }

}
