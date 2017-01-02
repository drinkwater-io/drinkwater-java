package drinkwater.core.internal;

import drinkwater.core.ServiceState;
import drinkwater.core.helper.Service;

import java.util.List;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface IServiceManagement {
    List<Service> getServices();

    List<String> getServiceNames();

    Service getService(String serviceName);

    String stopService(String serviceName);


    String startService(String serviceName);

    ServiceState getServiceState(String serviceName);
}
