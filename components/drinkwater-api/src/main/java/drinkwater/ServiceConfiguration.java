package drinkwater;


import drinkwater.helper.MapHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by A406775 on 23/12/2016.
 */
public class ServiceConfiguration implements IServiceConfiguration, IServiceBuilder, IMockBuilder {

    private String serviceName;

    private List<String> properties;

    private Class serviceClass;

    private Class targetBeanClass;

    private Object targetBean;

    private InjectionStrategy injectionStrategy;

    private ServiceScheme scheme;

    private List<String> serviceDependencies;

    private Properties initialProperties;



    public ServiceConfiguration() {
        injectionStrategy = InjectionStrategy.Default;
        properties = new ArrayList<String>();
        scheme = ServiceScheme.BeanClass;
        serviceDependencies = new ArrayList<>();
        initialProperties = new Properties();
    }

    public static ServiceConfiguration empty() {
        ServiceConfiguration sc = new ServiceConfiguration();
        sc.injectionStrategy = null;
        sc.properties = null;
        sc.scheme = null;
        sc.serviceDependencies = null;
        sc.properties = null;
        return sc;
    }


    public static ServiceConfiguration fromConfig(IServiceConfiguration config) {
        ServiceConfiguration sc = new ServiceConfiguration();
        sc.serviceName = config.getServiceName();
        sc.properties = Arrays.asList(config.getProperties());
        sc.serviceClass = config.getServiceClass();
        sc.targetBeanClass = config.getTargetBeanClass();
        sc.targetBean = config.getTargetBean();
        sc.injectionStrategy = config.getInjectionStrategy();
        sc.scheme = config.getScheme();
        sc.serviceDependencies = config.getServiceDependencies();

        return sc;

    }

    @Override
    public IServiceConfiguration patchWith(IServiceConfiguration patchConfig) {
        //TODO fixme : changing the name of the service can be potentially dangerous as it might be a dependency...
        this.serviceName = (patchConfig.getServiceName() == null) ? this.serviceName : patchConfig.getServiceName();
        this.properties = (patchConfig.getProperties().length == 0) ? this.properties : Arrays.asList(patchConfig.getProperties());
        this.serviceClass = (patchConfig.getServiceClass() == null) ? this.serviceClass : patchConfig.getServiceClass();
        this.targetBeanClass = (patchConfig.getTargetBeanClass() == null) ? this.targetBeanClass : patchConfig.getTargetBeanClass();
        this.targetBean = (patchConfig.getTargetBean() == null) ? this.targetBean : patchConfig.getTargetBean();
        this.injectionStrategy = (patchConfig.getInjectionStrategy() == null) ? this.injectionStrategy : patchConfig.getInjectionStrategy();
        this.scheme = (patchConfig.getScheme() == null) ? this.scheme : patchConfig.getScheme();
        this.serviceDependencies = (patchConfig.getServiceDependencies() == null) ? this.serviceDependencies : patchConfig.getServiceDependencies();
//        this.initialProperties = (patchConfig.getInitialProperties() == null) ? this.initialProperties : patchConfig.getInitialProperties();
        this.initialProperties = MapHelper.mergeProperties(this.initialProperties, patchConfig.getInitialProperties());
        return this;
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
    public IServiceBuilder asRemote() {
        this.scheme = ServiceScheme.Remote;
        return this;
    }

    @Override
    public IServiceBuilder withInjectionStrategy(InjectionStrategy strategy) {
        this.injectionStrategy = strategy;
        return this;
    }

    @Override
    public IServiceBuilder withProperty(String key, Object value) {
        this.addInitialProperty(key, value);
        return this;
    }

    @Override
    public ServiceConfiguration dependsOn(String... services) {

        for (String service : services) {
            this.serviceDependencies.add(service);
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
    public String[] getProperties() {
        if (properties != null) {
            return properties.toArray(new String[0]);
        }
        return new String[0];
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
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
    public List<String> getServiceDependencies() {
        return serviceDependencies;
    }

    @Override
    public Object getTargetBean() {
        return targetBean;
    }

    @Override
    public void setTargetBean(Object beanToUse) {
        targetBean = beanToUse;
    }

    @Override
    public Properties getInitialProperties() {
        return initialProperties;
    }

    @Override
    public void addInitialProperty(String key, Object value) {
        initialProperties.setProperty(key, value.toString());

        String test = initialProperties.getProperty(key);
    }

    @Override
    public String getServiceName() {
        if (serviceName == null) {
            //fallback to service class if present
            if (this.getServiceClass() != null) {
                return this.getServiceClass().getName();
            }
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

    public void with(Object mockObject) {
        this.setTargetBean(mockObject);
    }
}
