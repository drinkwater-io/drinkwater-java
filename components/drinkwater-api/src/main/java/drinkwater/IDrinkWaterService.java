package drinkwater;

import drinkwater.trace.BaseEvent;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface IDrinkWaterService {

    ITracer getTracer();

    IServiceConfiguration getConfiguration();

    void start();

    void stop();

    ServiceState getState();

    void sendEvent(BaseEvent event);

}
