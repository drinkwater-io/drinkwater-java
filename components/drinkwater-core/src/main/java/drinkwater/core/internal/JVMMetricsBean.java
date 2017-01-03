package drinkwater.core.internal;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.*;

import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * Created by A406775 on 3/01/2017.
 */
public class JVMMetricsBean {

    private MetricRegistry JVMregistry = new MetricRegistry();

    public JVMMetricsBean() {
        registerAll("gc", new GarbageCollectorMetricSet(), JVMregistry);
        registerAll("buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()), JVMregistry);
        registerAll("memory", new MemoryUsageGaugeSet(), JVMregistry);
        registerAll("threads", new ThreadStatesGaugeSet(), JVMregistry);
        JVMregistry.register("filedescriptors", new FileDescriptorRatioGauge());
    }

    private void registerAll(String prefix, MetricSet metricSet, MetricRegistry registry) {
        for (Map.Entry<String, Metric> entry : metricSet.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                registerAll(prefix + "." + entry.getKey(), (MetricSet) entry.getValue(), registry);
            } else {
                registry.register(prefix + "." + entry.getKey(), entry.getValue());
            }
        }
    }

    public MetricRegistry getMetrics() {
        return JVMregistry;
    }
}
