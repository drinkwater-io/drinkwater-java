package drinkwater.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A406775 on 23/12/2016.
 */
public class ServiceConfiguration {

    private String properties;

    private Class serviceClass;

    private Class targetBean;

    private InjectionStrategy injectionStrategy = InjectionStrategy.None;

    private ServiceScheme scheme = ServiceScheme.BeanClass; //default to bean

    private List<ServiceConfiguration> serviceDependencies = new ArrayList<>();

    private ServiceConfiguration(){}

    public static ServiceConfiguration forService(Class serviceClass){
        ServiceConfiguration sc = new ServiceConfiguration();
        sc.serviceClass = serviceClass;
        return sc;
    }

    public ServiceConfiguration withProperties(String propertyFile){
        this.properties = propertyFile;
        return this;
    }

    public ServiceConfiguration useBean(Class bean){
        this.targetBean = bean;
        this.scheme = ServiceScheme.BeanClass;
        return this;
    }

    public ServiceConfiguration withInjectionStrategy(InjectionStrategy strategy){
        this.injectionStrategy = strategy;
        return this;
    }

    public ServiceConfiguration dependsOn(ServiceConfiguration... configs){

        for (ServiceConfiguration conf:configs) {
            this.serviceDependencies.add(conf);
        }

        return this;
    }


    @Override
    public String toString() {
        return "ServiceConfiguration{" +
                "properties='" + properties + '\'' +
                '}';
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public String getProperties() {
        return properties;
    }

    public Class getTargetBean() {
        return targetBean;
    }

    public ServiceScheme getScheme() {
        return scheme;
    }

    public InjectionStrategy getInjectionStrategy() {
        return injectionStrategy;
    }

    public List<ServiceConfiguration> getServiceDependencies() {
        return serviceDependencies;
    }
}
