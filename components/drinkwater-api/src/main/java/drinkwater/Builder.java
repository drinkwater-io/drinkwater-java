package drinkwater;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Builder<T extends Builder> {

    private ServiceRepository serviceRepository;

    private CamelContext camelContext;

    Map<String, Object> properties = new HashMap<>();

    private String name;

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

    public T with(String constantKey, Object val) {
        properties.computeIfAbsent(constantKey, (key) -> val);
        return (T) this;
    }

    public <V> V lookupProperty(Class type, String key, V value) {

        Object result = properties.getOrDefault(key, value);
        return (V) getCamelContext().getTypeConverter().convertTo(type, result);
    }


    public abstract void configureRouteBuilder(RouteBuilder rb) ;

    public abstract void configureMethodEndpoint(RouteBuilder rb, Method method) ;

    public Object getBean() {
        try {
            return BeanFactory.buildBean(getServiceRepository(), getServiceClass());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
