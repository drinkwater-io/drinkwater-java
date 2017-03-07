package drinkwater.feature.trace;

import drinkwater.FeatureBuilder;
import drinkwater.IBaseEventLogger;

public class TraceFeatureBuilder implements FeatureBuilder<TraceFeature>{

    TraceFeature traceFeature = new TraceFeature();

    public static TraceFeatureBuilder trace() {
        return new TraceFeatureBuilder();
    }

    public TraceFeatureBuilder withLogger(Class<? extends IBaseEventLogger> clazz){
        try {
            IBaseEventLogger logger = clazz.newInstance();
            traceFeature.setLogger(logger);
        }
        catch(Exception ex){
            throw new RuntimeException("could not configure logger", ex);
        }
        return this;
    }

    @Override
    public TraceFeature getFeature() {
        return traceFeature;
    }
}
