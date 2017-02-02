package drinkwater.trace;

import drinkwater.IBaseEventLogger;

import java.util.ArrayList;
import java.util.List;

public class MockEventLogger implements IBaseEventLogger {

    private List<BaseEvent> events = new ArrayList<>();

    @Override
    public void logEvent(BaseEvent event) {
        events.add(event);
    }

    public List<BaseEvent> getEvents() {
        return events;
    }
}
