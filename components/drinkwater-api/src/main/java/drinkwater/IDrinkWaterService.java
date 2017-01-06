package drinkwater;

import drinkwater.trace.Payload;

import java.lang.reflect.Method;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface IDrinkWaterService {

    ITracer getTracer();

    IServiceConfiguration getConfiguration();

    void start();

    void stop();

    ServiceState getState();

    Boolean sendEvent(Class eventClass, Method method, Payload payload);
}
