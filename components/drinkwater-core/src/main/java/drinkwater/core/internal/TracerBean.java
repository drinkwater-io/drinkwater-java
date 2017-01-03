package drinkwater.core.internal;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import drinkwater.ITracer;
import org.apache.camel.Exchange;

import static drinkwater.DrinkWaterConstants.BeanOperationName;
import static drinkwater.DrinkWaterConstants.MetricsOperationTimer;

/**
 * Created by A406775 on 2/01/2017.
 */
public class TracerBean implements ITracer {

    public MetricRegistry metrics = new MetricRegistry();

    public void start(Object exchange) {
        if (exchange != null && exchange instanceof Exchange) {
            Exchange exchange1 = (Exchange) exchange;
            Object uriName = exchange1.getIn().getHeader(BeanOperationName);
            if (uriName != null) {
                exchange1.getIn().setHeader(MetricsOperationTimer, createTimerContext((String) uriName));
            }
        }

    }

    public void stop(Object exchange) {
        if (exchange != null && exchange instanceof Exchange) {
            Exchange exchange1 = (Exchange) exchange;
            Object timerContext = exchange1.getIn().getHeader(MetricsOperationTimer);
            if (timerContext != null) {
                ((Timer.Context) timerContext).stop();
            }
        }
    }

    public MetricRegistry getMetrics() {
        return metrics;
    }

    public Timer.Context createTimerContext(String name) {
        return metrics.timer(name).time();
    }
}
