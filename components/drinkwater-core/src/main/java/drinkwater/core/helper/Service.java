package drinkwater.core.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import drinkwater.IServiceConfiguration;
import drinkwater.InjectionStrategy;
import drinkwater.ServiceScheme;
import drinkwater.core.RouteBuilders;
import drinkwater.core.ServiceRepository;
import drinkwater.core.ServiceState;
import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by A406775 on 29/12/2016.
 */
public class Service implements IServiceConfiguration {



    IServiceConfiguration serviceConfiguration;

    @JsonIgnore
    DefaultCamelContext camelContext;
    //lazy initialized
    @JsonIgnore
    PropertiesComponent propertiesComponent;

    @JsonIgnore
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private ServiceState state = ServiceState.NotStarted;

    public Service(DefaultCamelContext fromContext, IServiceConfiguration serviceConfiguration) {
        this.camelContext =fromContext;
        this.serviceConfiguration = serviceConfiguration;
    }

    public Service(IServiceConfiguration serviceConfiguration) {
        this.camelContext = new DefaultCamelContext();
        this.camelContext.disableJMX();
        this.camelContext.setName("CAMEL-CONTEXT-" + serviceConfiguration.getServiceClass().getName());
        this.serviceConfiguration = serviceConfiguration;
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    @Override
    public Class getServiceClass() {
        return serviceConfiguration.getServiceClass();
    }

    @Override
    public Object getTargetBean() {
        return serviceConfiguration.getTargetBean();
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
    public String getServiceName(){
        return serviceConfiguration.getServiceName();
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

    public void start() {
        try {
            this.getCamelContext().start();
            this.state = ServiceState.Up;
        } catch (Exception e) {
            throw new RuntimeException("Could not start service", e);
        }
    }

    public void stop()  {
        try {
            this.getCamelContext().stop();
            this.state = ServiceState.Stopped;
        } catch (Exception e) {
            throw new RuntimeException("could not stop service : ", e);
        }
    }

    public void configure(ServiceRepository app) throws Exception {
        if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanObject) {
           //nothing to configure here
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanClass) {
            this.camelContext.addRoutes(RouteBuilders.mapBeanClassRoutes(app, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Rest) {
            this.camelContext.addRoutes(RouteBuilders.mapRestRoutes(app, this));
        }
    }

    public ServiceState getState() {
        return state;
    }
}
