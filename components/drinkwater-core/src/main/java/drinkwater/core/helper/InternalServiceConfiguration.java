package drinkwater.core.helper;

import drinkwater.IServiceConfiguration;
import drinkwater.InjectionStrategy;
import drinkwater.ServiceScheme;
import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;

import java.util.List;

/**
 * Created by A406775 on 29/12/2016.
 */
public class InternalServiceConfiguration implements IServiceConfiguration {

    IServiceConfiguration serviceConfiguration;

    CamelContext camelContext;

    //lazy initialized
    PropertiesComponent propertiesComponent;

    public InternalServiceConfiguration(IServiceConfiguration serviceConfiguration, CamelContext camelContext) {
        this.serviceConfiguration = serviceConfiguration;
        this.camelContext = camelContext;
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    @Override
    public Class getServiceClass() {
        return serviceConfiguration.getServiceClass();
    }

    @Override
    public String getProperties() {
        return serviceConfiguration.getProperties();
    }

    @Override
    public Class getTargetBeanClass() {
        return serviceConfiguration.getTargetBeanClass();
    }

    @Override
    public ServiceScheme getScheme() {
        return serviceConfiguration.getScheme();
    }

    @Override
    public InjectionStrategy getInjectionStrategy() {
        return serviceConfiguration.getInjectionStrategy();
    }

    @Override
    public List<IServiceConfiguration> getServiceDependencies() {
        return serviceConfiguration.getServiceDependencies();
    }

    public PropertiesComponent getPropertiesComponent() {
        if (propertiesComponent == null) {
            propertiesComponent = camelContext.getComponent(
                    "properties", PropertiesComponent.class);
            propertiesComponent.setLocation(this.getProperties());
        }
        return propertiesComponent;
    }

    public String lookupProperty(String s) throws Exception {
        return getPropertiesComponent().parseUri(s);
    }
}
