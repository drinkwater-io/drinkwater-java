package drinkwater;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by A406775 on 23/12/2016.
 */
public class ServiceConfiguration implements IServiceConfiguration, IServiceBuilder {

    private String serviceName;

    private List<String> properties = new ArrayList<String>();

    private Class serviceClass;

    private Class targetBeanClass;

    private Object targetBean;

    private InjectionStrategy injectionStrategy = InjectionStrategy.Default;

    private ServiceScheme scheme = ServiceScheme.BeanClass; //default to bean

    private List<IServiceConfiguration> serviceDependencies = new ArrayList<>();

    public ServiceConfiguration() {
    }

    public static ServiceConfiguration fromConfig(IServiceConfiguration config) {
        ServiceConfiguration sc = new ServiceConfiguration();
        sc.serviceName = config.getServiceName();
        sc.properties = Arrays.asList(config.getPropertiesLocations());
        sc.serviceClass = config.getServiceClass();
        sc.targetBeanClass = config.getTargetBeanClass();
        sc.targetBean = config.getTargetBean();
        sc.injectionStrategy = config.getInjectionStrategy();
        sc.scheme = config.getScheme();
        sc.serviceDependencies = sc.getServiceDependencies();

        return sc;

    }

    public IServiceBuilder forService(Class serviceClass) {
        this.serviceClass = serviceClass;
        return this;
    }

    @Override
    public IServiceBuilder useBeanClass(Class bean) {
        this.targetBeanClass = bean;
        this.scheme = ServiceScheme.BeanClass;
        return this;
    }


    @Override
    public ServiceConfiguration name(String name) {
        this.serviceName = name;
        return this;
    }

    @Override
    public IServiceBuilder withProperties(String propertyFile) {
        this.properties.add(propertyFile);
        return this;
    }


    @Override
    public ServiceConfiguration useBean(Object bean) {
        this.targetBean = bean;
        this.targetBeanClass = bean.getClass();
        this.scheme = ServiceScheme.BeanObject;
        return this;
    }

    @Override
    public IServiceBuilder asRest() {
        this.scheme = ServiceScheme.Rest;
        return this;
    }

    @Override
    public IServiceBuilder withInjectionStrategy(InjectionStrategy strategy) {
        this.injectionStrategy = strategy;
        return this;
    }

    @Override
    public ServiceConfiguration dependsOn(IServiceConfiguration... configs) {

        for (IServiceConfiguration conf : configs) {
            this.serviceDependencies.add(conf);
        }

        return this;
    }


    @Override
    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    @Override
    public String[] getPropertiesLocations() {
        return properties.toArray(new String[0]);
    }

    @Override
    public Class getTargetBeanClass() {
        return targetBeanClass;
    }

    public void setTargetBeanClass(Class targetBeanClass) {
        this.targetBeanClass = targetBeanClass;
    }

    @Override
    public ServiceScheme getScheme() {
        return scheme;
    }

    public void setScheme(ServiceScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public InjectionStrategy getInjectionStrategy() {
        return injectionStrategy;
    }

    public void setInjectionStrategy(InjectionStrategy injectionStrategy) {
        this.injectionStrategy = injectionStrategy;
    }

    @Override
    public List<IServiceConfiguration> getServiceDependencies() {
        return serviceDependencies;
    }

    public void setServiceDependencies(List<IServiceConfiguration> serviceDependencies) {
        this.serviceDependencies = serviceDependencies;
    }

    @Override
    public Object getTargetBean() {
        return targetBean;
    }

    public void setTargetBean(Object beanToUse) {
        targetBean = beanToUse;
    }

    @Override
    public String getServiceName() {
        if(serviceName == null){
            return this.getServiceClass().getSimpleName();
        }
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "ServiceConfiguration{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceClass=" + serviceClass +
                '}';
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
