package drinkwater.core.internal;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import drinkwater.IDrinkWaterService;
import drinkwater.ServiceState;
import drinkwater.rest.HttpMethod;
import drinkwater.rest.NoBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceManagementBean implements IServiceManagement {

    private List<IDrinkWaterService> IDrinkWaterServices = new ArrayList<>();

    private MetricRegistry metricsregistry;

    private MetricRegistry jvmMetricsRegistry;


    public ServiceManagementBean(List<IDrinkWaterService> IDrinkWaterServices,
                                 MetricRegistry metricsRegistry,
                                 MetricRegistry jvmMetricsRegistry) {
        this.metricsregistry = metricsRegistry;
        this.jvmMetricsRegistry = jvmMetricsRegistry;
        this.IDrinkWaterServices = IDrinkWaterServices;
    }

    @Override
    public List<IDrinkWaterService> getServices() {

        return IDrinkWaterServices;
    }

    @Override
    public List<String> getServiceNames() {
        List<String> result = javaslang.collection.List.ofAll(IDrinkWaterServices)
                .map(s -> s.getConfiguration().getServiceName())
                .toJavaList();

        return result;
    }

    @Override
    public IDrinkWaterService getService(String serviceName) {
        IDrinkWaterService svc = javaslang.collection.List.ofAll(IDrinkWaterServices)
                .filter(s -> s.getConfiguration().getServiceName().equals(serviceName))
                .get();

        return svc;
    }

    @HttpMethod("POST")
    @NoBody
    @Override
    public String stopService(String serviceName) {

        //find service
        IDrinkWaterService svc = getService(serviceName);

        svc.stop();

        return String.format("Service %s stopped", serviceName);
    }

    @HttpMethod("POST")
    @NoBody
    @Override
    public String startService(String serviceName) {
        IDrinkWaterService svc = getService(serviceName);

        svc.start();

        return String.format("Service %s started", serviceName);
    }

    @Override
    public ServiceState getServiceState(String serviceName) {
        return getService(serviceName).getState();
    }

    @Override
    public MetricRegistry getMetrics() {

        return metricsregistry;
    }

    @Override
    public MetricRegistry getJvm() {

        return jvmMetricsRegistry;
    }

    @Override
    public Map<String, Timer> getTimers() {
        return metricsregistry.getTimers();
    }
}
