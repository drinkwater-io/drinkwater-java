package drinkwater;


import javaslang.collection.List;

import java.util.ArrayList;

/**
 * Created by A406775 on 29/12/2016.
 */
public class ApplicationBuilder implements IApplicationBuilder {

    private String applicationName;

    private boolean useTracing;

    private boolean useServiceManagement;

    private Class eventLoggerClass;

    private java.util.List<ServiceConfiguration> configurations = new ArrayList<>();

    private java.util.List<IDataStoreConfiguration> dataStoreConfigs = new ArrayList<>();

    public ApplicationBuilder() {
    }

    public ApplicationBuilder(java.util.List<IServiceConfiguration> configurations) {
        this.configurations = toServiceConfiguration(configurations);
    }

    public void addConfiguration(ServiceConfiguration configuration) {

        this.configurations.add(configuration);
    }

    public void addConfigurations(java.util.List<ServiceConfiguration> configurations) {
        this.configurations.addAll(configurations);
    }

    public final java.util.List<IServiceConfiguration> getConfigurations() {
        return List.ofAll(this.configurations).map(s -> (IServiceConfiguration) s).toJavaList();
    }

    public java.util.List<IDataStoreConfiguration> getStores() {
        return dataStoreConfigs;
    }

    public IServiceConfiguration getConfiguration(String serviceName) {
        return List.ofAll(getConfigurations())
                .filter(conf -> conf.getServiceName().equals(serviceName))
                .get();
    }

    public IServiceBuilder getBuilder(Class serviceClass) {
        return List.ofAll(configurations)
                .filter(conf -> conf.getServiceClass().equals(serviceClass))
                .get();
    }

    public IServiceBuilder getBuilder(String serviceName) {
        return List.ofAll(configurations)
                .filter(conf -> conf.getServiceName().equals(serviceName))
                .get();
    }

    private java.util.List<ServiceConfiguration> toServiceConfiguration(java.util.List<IServiceConfiguration> configurations) {
        return List.ofAll(configurations).map(s -> ServiceConfiguration.fromConfig(s)).toJavaList();
    }


    public IServiceBuilder addService(Class interfaceClass) {
        ServiceConfiguration configuration = new ServiceConfiguration();
        configuration.setServiceClass(interfaceClass);
        addConfiguration(configuration);
        return configuration;
    }

    public IServiceBuilder addService(String serviceName) {
        ServiceConfiguration configuration = new ServiceConfiguration();
        configuration.setServiceName(serviceName);
        addConfiguration(configuration);
        return configuration;
    }

    public IServiceBuilder addService(String serviceName, Class interfaceClass) {
        IServiceBuilder configuration = addService(serviceName);
        return configuration.forService(interfaceClass);
    }

    public IServiceBuilder addService(String serviceName, Class interfaceClass, Object beanToUse) {
        IServiceBuilder configuration = addService(serviceName, interfaceClass);
        return configuration.useBean(beanToUse);
    }

    public IServiceBuilder addService(String serviceName, Class interfaceClass, Object beanToUse, String... dependencies) {
        IServiceBuilder configuration = addService(serviceName, interfaceClass);
        configuration.dependsOn(dependencies);
        return configuration.useBean(beanToUse);
    }

    public IServiceBuilder addService(String serviceName, Class interfaceClass, Class beanClass) {
        IServiceBuilder configuration = addService(serviceName, interfaceClass);
        return configuration.useBeanClass(beanClass);
    }

    public IServiceBuilder addService(String serviceName, Class interfaceClass, Class beanClass, String... dependencies) {
        IServiceBuilder configuration = addService(serviceName, interfaceClass);
        configuration.dependsOn(dependencies);
        return configuration.useBeanClass(beanClass);
    }

    public IServiceBuilder addService(String serviceName, Class interfaceClass, Object beanToUse, String propertiesLocation, InjectionStrategy injectionStrategy) {
        IServiceBuilder configuration = addService(serviceName, interfaceClass, beanToUse, propertiesLocation);
        return configuration.withInjectionStrategy(injectionStrategy);
    }

    public IServiceBuilder addService(String serviceName, Class interfaceClass, Class beanClass, String propertiesLocation, InjectionStrategy injectionStrategy) {
        IServiceBuilder configuration = addService(serviceName, interfaceClass);
        configuration.withProperties(propertiesLocation);
        configuration.withInjectionStrategy(injectionStrategy);
        configuration.useBeanClass(beanClass);
        return configuration.withInjectionStrategy(injectionStrategy);
    }


    public void changeScheme(ServiceScheme newScheme) {
        javaslang.collection.List.ofAll(configurations)
                .forEach(c -> c.setScheme(newScheme));
    }

    public void changeInjectionStrategy(InjectionStrategy injectionStrategy) {
        javaslang.collection.List.ofAll(configurations)
                .forEach(c -> c.setInjectionStrategy(injectionStrategy));
    }

    protected void addStore(String tt, Class implementingClass) {
        DefaultDataStoreConfiguration dsc = new DefaultDataStoreConfiguration();
        dsc.setName(tt);
        dsc.setImplementingClass(implementingClass);
        dataStoreConfigs.add(dsc);
    }

    public Class getEventLoggerClass() {
        return eventLoggerClass;
    }

    public void useEventLogger(Class eventLoggerClass) {
        this.eventLoggerClass = eventLoggerClass;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public boolean isUseTracing() {
        return useTracing;
    }

    public void useTracing(boolean useTracing) {
        this.useTracing = useTracing;
    }

    public boolean isUseServiceManagement() {
        return useServiceManagement;
    }

    public void setUseServiceManagement(boolean useServiceManagement) {
        this.useServiceManagement = useServiceManagement;
    }

    public void configure() {
    }

}
