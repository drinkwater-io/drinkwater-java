package drinkwater.core.internal;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import drinkwater.ITracer;
import org.apache.camel.Exchange;

/**
 * Created by A406775 on 2/01/2017.
 */
public class TracerBean implements ITracer {

    public MetricRegistry metrics = new MetricRegistry();

    public void start(Object exchange) {
        Exchange exchange1 = (Exchange) exchange;
        String uriName = (String) exchange1.getIn().getHeader("CamelHttpUri");
        exchange1.getIn().setHeader("DW-REST-TRACER", createTimerContext(uriName));
    }

    public void stop(Object exchange) {
        Exchange exchange1 = (Exchange) exchange;
        Timer.Context ctx = (Timer.Context) exchange1.getIn().getHeader("DW-REST-TRACER");
        ctx.stop();
    }

    public MetricRegistry getMetrics() {
        return metrics;
    }

    public Timer.Context createTimerContext(String name) {
        return metrics.timer(name).time();
    }
}
