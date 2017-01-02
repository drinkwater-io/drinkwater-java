package drinkwater.core.internal;

import drinkwater.core.ServiceState;
import drinkwater.core.helper.Service;
import drinkwater.rest.HttpMethod;
import drinkwater.rest.NoBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceManagementBean implements IServiceManagement {

    public List<Service> services = new ArrayList<>();

    public ServiceManagementBean() {
    }

    public ServiceManagementBean(List<Service> services) {
        this.services = services;
    }

    @Override
    public List<Service> getServices() {

        return services;
    }

    @Override
    public List<String> getServiceNames() {
        List<String> result = javaslang.collection.List.ofAll(services)
                .map(s -> s.getServiceName())
                .toJavaList();

        return result;
    }

    @Override
    public Service getService(String serviceName) {
        Service svc = javaslang.collection.List.ofAll(services)
                .filter(s -> s.getServiceName().equals(serviceName))
                .get();

        return svc;
    }

    @HttpMethod("POST")
    @NoBody
    @Override
    public String stopService(String serviceName) {

        //find service
        Service svc = getService(serviceName);

        svc.stop();

        return String.format("Service %s stopped", serviceName);
    }

    @HttpMethod("POST")
    @NoBody
    @Override
    public String startService(String serviceName) {
        Service svc = getService(serviceName);

        svc.start();

        return String.format("Service %s started", serviceName);
    }

    @Override
    public ServiceState getServiceState(String serviceName) {
        return getService(serviceName).getState();
    }
}
