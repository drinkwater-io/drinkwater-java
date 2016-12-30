package drinkwater;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by A406775 on 23/12/2016.
 */
public class ServiceConfiguration implements IServiceConfiguration {

    private String properties;

    private Class serviceClass;

    private Class targetBeanClass;

    private Object targetBean;

    private InjectionStrategy injectionStrategy = InjectionStrategy.None;

    private ServiceScheme scheme = ServiceScheme.BeanClass; //default to bean

    private List<IServiceConfiguration> serviceDependencies = new ArrayList<>();

    protected ServiceConfiguration(){}

    public static ServiceConfiguration forService(Class serviceClass){
        ServiceConfiguration sc = new ServiceConfiguration();
        sc.serviceClass = serviceClass;
        return sc;
    }

    public ServiceConfiguration withProperties(String propertyFile){
        this.properties = propertyFile;
        return this;
    }

    public ServiceConfiguration useBeanClass(Class bean){
        this.targetBeanClass = bean;
        this.scheme = ServiceScheme.BeanClass;
        return this;
    }

    public ServiceConfiguration useBean(Object bean){
        this.targetBean = bean;
        this.scheme = ServiceScheme.BeanObject;
        return this;
    }

    public ServiceConfiguration asRest(){
        this.scheme = ServiceScheme.Rest;
        return this;
    }

    public IServiceConfiguration withInjectionStrategy(InjectionStrategy strategy){
        this.injectionStrategy = strategy;
        return this;
    }

    public IServiceConfiguration dependsOn(IServiceConfiguration... configs){

        for (IServiceConfiguration conf:configs) {
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

    @Override
    public Class getServiceClass() {
        return serviceClass;
    }

    @Override
    public String getProperties() {
        return properties;
    }

    @Override
    public Class getTargetBeanClass() {
        return targetBeanClass;
    }

    @Override
    public ServiceScheme getScheme() {
        return scheme;
    }

    @Override
    public InjectionStrategy getInjectionStrategy() {
        return injectionStrategy;
    }

    @Override
    public List<IServiceConfiguration> getServiceDependencies() {
        return serviceDependencies;
    }

}
