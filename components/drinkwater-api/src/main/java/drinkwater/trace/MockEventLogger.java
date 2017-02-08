package drinkwater.trace;

import drinkwater.IBaseEventLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class MockEventLogger implements IBaseEventLogger {

    private List<BaseEvent> events = new ArrayList<>();

    @Override
    public void logEvent(BaseEvent event) {
        events.add(event);
    }

    public List<BaseEvent> getEvents() {
        return events;
    }

    public int count(){
        return getEvents().size();
    }

    public boolean containsAnyEventOfType(Class clazz){
        Optional<BaseEvent> optionalEvent = getEvents()
                .stream().filter(e -> e.getClass().equals(clazz))
                .findAny();
        return optionalEvent.isPresent();
    }

    public List<BaseEvent> getEventsOfType(Class clazz){
        List<BaseEvent> events = getEvents()
                .stream().filter(e -> e.getClass().equals(clazz))
                .collect(toList());
        return events;
    }
}
