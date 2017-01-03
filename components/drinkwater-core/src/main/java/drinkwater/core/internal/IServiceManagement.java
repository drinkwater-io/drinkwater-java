package drinkwater.core.internal;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import drinkwater.IDrinkWaterService;
import drinkwater.ServiceState;

import java.util.List;
import java.util.Map;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface IServiceManagement {
    List<IDrinkWaterService> getServices();

    List<String> getServiceNames();

    IDrinkWaterService getService(String serviceName);

    String stopService(String serviceName);

    String startService(String serviceName);

    ServiceState getServiceState(String serviceName);

    MetricRegistry getMetrics();

    MetricRegistry getJvm();

    Map<String, Timer> getTimers();
}
