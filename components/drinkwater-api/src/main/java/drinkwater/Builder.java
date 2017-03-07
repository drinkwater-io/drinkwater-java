package drinkwater;

import javafx.util.BuilderFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class Builder<T extends Builder> {

    private ServiceRepository serviceRepository;

    private CamelContext camelContext;

    Map<String, String> properties = new HashMap<>();

    List<Feature> features = new ArrayList<>();

    private String name;
    private Configuration configuration;
    private Object object;

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public ServiceRepository getServiceRepository() {
        return serviceRepository;
    }

    public void setServiceRepository(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    private Class serviceClass;

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Builder named(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public <B extends FeatureBuilder> Builder<T> use(B featureBuilder) {
        try {
            features.add(featureBuilder.getFeature());
            return this;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public T with(String constantKey, String val) {
        properties.computeIfAbsent(constantKey, (key) -> val);
        return (T) this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public <V> V lookupProperty(Class type, String key) {

        String prefix = name;
        return getConfiguration().lookupProperty(type, prefix + "." + key);
    }

    public String addProperty(String key, String val){
        String prefix = name;
        return getConfiguration().addProperty(prefix + "." + key, val);
    }


    public Object getBean() {
        try {
            if(getObject() != null){
                return BeanFactory.configureBean(getServiceRepository(), getObject());
            }
            return BeanFactory.buildBean(getServiceRepository(), getServiceClass());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void start(){}

    public void stop(){}

    public void beforeExposeService(RouteBuilder rb) {

    }

    public RouteDefinition exposeService(RouteBuilder rb, Method method) {
        return null;
    }

    public void targetService(RouteDefinition processDefinition, Method method) {
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
}
