package test.drinkwater.core.model.forTracing;

import drinkwater.IBaseEventLogger;
import drinkwater.ServiceDependency;
import drinkwater.trace.BaseEvent;

public class CustomTraceClass implements IBaseEventLogger {

    @ServiceDependency
    public IServiceC serviceC;

    public static int called;
    @Override
    public void logEvent(BaseEvent event) {
        if(serviceC == null){
            return;
        }
        called++;
    }
}
