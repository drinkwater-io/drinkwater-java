package drinkwater.core.internal;

import com.codahale.metrics.MetricRegistry;
import drinkwater.IDrinkWaterService;
import drinkwater.ServiceState;

import java.util.List;

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
}
