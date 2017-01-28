package drinkwater;


import drinkwater.helper.GeneralUtils;
import drinkwater.helper.MapUtils;

import java.nio.file.Paths;
import java.util.*;


public class ServiceConfiguration implements IServiceConfiguration, IServiceBuilder, IMockBuilder, IRoutingBuilder {

    private String serviceName;

    private ArrayList<String> properties;

    private Class serviceClass;

    private Class targetBeanClass;

    private Object targetBean;

    private InjectionStrategy injectionStrategy;

    private ServiceScheme scheme;

    private List<String> serviceDependencies;

    private Properties initialProperties;

    private String cronExpression;

    private int repeatInterval = 1000;

    private Boolean traceEvent = false;

    private String routingHeader;

    private Map<String, String> routingMap = new HashMap<>();

    //FIXME : this is a bad way to pass the host of service for routingScheme
    private String serviceHost;


    public ServiceConfiguration() {
        injectionStrategy = InjectionStrategy.Default;
        properties = new ArrayList<>();
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
        sc.traceEvent = null;
        return sc;
    }


    static ServiceConfiguration fromConfig(IServiceConfiguration config) {
        ServiceConfiguration sc = new ServiceConfiguration();
        sc.serviceName = config.getServiceName();
        sc.properties = new ArrayList<>(Arrays.asList(config.getPropertiesLocations()));
        sc.serviceClass = config.getServiceClass();
        sc.targetBeanClass = config.getTargetBeanClass();
        sc.targetBean = config.getTargetBean();
        sc.injectionStrategy = config.getInjectionStrategy();
        sc.scheme = config.getScheme();
        sc.serviceDependencies = config.getServiceDependencies();
        sc.traceEvent = config.getIsTraceEnabled();

        return sc;

    }

    @Override
    public IServiceConfiguration patchWith(IServiceConfiguration patchConfig) {
        //TODO fixme : changing the name of the service can be potentially dangerous as it might be a dependency...
        this.serviceName = (patchConfig.getServiceName() == null) ? this.serviceName : patchConfig.getServiceName();
        this.properties = (patchConfig.getPropertiesLocations().length == 0) ? this.properties : new ArrayList<>(Arrays.asList(patchConfig.getPropertiesLocations()));
        this.serviceClass = (patchConfig.getServiceClass() == null) ? this.serviceClass : patchConfig.getServiceClass();
        this.targetBeanClass = (patchConfig.getTargetBeanClass() == null) ? this.targetBeanClass : patchConfig.getTargetBeanClass();
        this.targetBean = (patchConfig.getTargetBean() == null) ? this.targetBean : patchConfig.getTargetBean();
        this.injectionStrategy = (patchConfig.getInjectionStrategy() == null) ? this.injectionStrategy : patchConfig.getInjectionStrategy();
        this.scheme = (patchConfig.getScheme() == null) ? this.scheme : patchConfig.getScheme();
        this.traceEvent = (patchConfig.getIsTraceEnabled() == null) ? this.traceEvent : patchConfig.getIsTraceEnabled();
        this.serviceDependencies = (patchConfig.getServiceDependencies() == null) ? this.serviceDependencies : patchConfig.getServiceDependencies();
        this.initialProperties = MapUtils.mergeProperties(this.initialProperties, patchConfig.getInitialProperties());
        return this;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public int getRepeatInterval() {
        return repeatInterval;
    }

    @Override
    public Boolean getIsTraceEnabled() {
        return traceEvent;
    }

    @Override
    public void setIsTraceEnabled(Boolean traceEvent) {
        this.traceEvent = traceEvent;
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
    public IServiceBuilder useTracing(Boolean traceEvent) {
        this.traceEvent = traceEvent;
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
    public IServiceBuilder asHttpProxy() {
        this.scheme = ServiceScheme.HttpProxy;
        this.serviceClass = IEmptyService.class;
        this.targetBeanClass = EmptyServiceImpl.class;
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
    public IServiceBuilder addInitialProperty(String key, Object value) {
        initialProperties.setProperty(key, value.toString());
        return this;
    }


    @Override
    public ServiceConfiguration dependsOn(String... services) {

        Collections.addAll(this.serviceDependencies, services);

        return this;
    }

    @Override
    public IServiceBuilder cron(String cronExpression) {

        this.scheme = ServiceScheme.Task;
        this.cronExpression = cronExpression;
        return this;
    }

    @Override
    public IServiceBuilder repeat(int repeatInterval) {
        this.scheme = ServiceScheme.Task;
        this.repeatInterval = repeatInterval;
        return this;
    }

    @Override
    public IRoutingBuilder asRouteur() {
        this.scheme = ServiceScheme.Routeur;
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
        if (properties == null) {
            properties = new ArrayList<>();
        }

        //default file from classpath
        properties.add(0, "classpath:" + getServiceName() + ".properties");
        //default file from file
        properties.add(1,"file:" +
                Paths.get( GeneralUtils.getJarFolderPath(this.getClass()).toString() ,
                        getServiceName() + ".properties"));

        return properties.toArray(new String[0]);
    }

    @Override
    public String getpropertiesPrefix() {
        return getPropertiesDefaultName();
    }

    @Override
    public String getPropertiesDefaultName() {
        return getServiceName();
    }

    public void setProperties(List<String> properties) {
        this.properties = new ArrayList<>(properties);
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

//    @Override
//    public void addInitialProperty(String key, Object value) {
//        initialProperties.setProperty(key, value.toString());
//
//    }

    @Override
    public String getServiceName() {
        if (serviceName == null) {
            //fallback to service class if present
            if (this.getServiceClass() != null) {
                return this.getTargetBeanClass().getSimpleName();
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

    @Override
    public IRoutingBuilder useHeader(String routingheader) {
        this.routingHeader = routingheader;
        return this;
    }

    @Override
    public IRoutingBuilder route(String headerValue, String serviceName) {
        this.routingMap.put(headerValue, serviceName);
        return this;
    }

    @Override
    public String getRoutingHeader() {
        return routingHeader;
    }

    @Override
    public Map<String, String> getRoutingMap() {
        return routingMap;
    }

    @Override
    public String getServiceHost() {
        return serviceHost;
    }

    @Override
    public void setServiceHost(String host) {
        this.serviceHost = host;
    }
}
