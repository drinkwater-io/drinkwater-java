package drinkwater.core.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import drinkwater.IServiceConfiguration;
import drinkwater.ITracer;
import drinkwater.ServiceScheme;
import drinkwater.ServiceState;
import drinkwater.common.tracing.TraceRouteBuilder;
import drinkwater.core.CamelContextFactory;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.internal.RouteBuilders;
import drinkwater.core.security.SimpleTokenValidation;
import drinkwater.trace.ClientReceivedEvent;
import drinkwater.trace.ClientSentEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static drinkwater.DrinkWaterPropertyConstants.*;
import static drinkwater.common.tracing.TraceRouteBuilder.addClientReceivedTracing;
import static drinkwater.common.tracing.TraceRouteBuilder.addClientSentTracing;

/**
 * Created by A406775 on 29/12/2016.
 */
public class Service implements drinkwater.IDrinkWaterService {

    @JsonIgnore
    DrinkWaterApplication _dwa;
    @JsonIgnore
    DefaultCamelContext camelContext;
    @JsonIgnore
    PropertiesComponent propertiesComponent;
    @JsonIgnore
    private static Logger logger = LoggerFactory.getLogger(Service.class);
    @JsonIgnore
    private ITracer tracer;

    private IServiceConfiguration serviceConfiguration;

    private ServiceState state = ServiceState.NotStarted;

    public Service(IServiceConfiguration serviceConfiguration, ITracer tracer, DrinkWaterApplication dwa) {
        this.serviceConfiguration = serviceConfiguration;
        this.camelContext = CamelContextFactory.createCamelContext(dwa, serviceConfiguration);
        this.tracer = tracer;
        this._dwa = dwa;
        this.addAuthenticationIfEnabled();
    }

    private void addAuthenticationIfEnabled()  {
        try {
            Boolean authEnabled = Boolean.parseBoolean(this.lookupProperty(Authenticate_Enabled));

            if (authEnabled) {

                String secret = this.safeLookupProperty(String.class, Authentication_Token_Encryption_Key, null);

                SimpleTokenValidation tokenProvider = new SimpleTokenValidation(secret);

                CamelContextFactory.registerBean(this.getCamelContext(),
                        Authentication_Token_Provider,
                        tokenProvider);
            }
        }
        catch(Exception ex){
            throw new RuntimeException("could not ad authentication", ex);
        }
    }


    public CamelContext getCamelContext() {
        return camelContext;
    }

    @Override
    public ITracer getTracer() {
        return tracer;
    }

    public PropertiesComponent getPropertiesComponent() {
        if (propertiesComponent == null) {
            propertiesComponent = camelContext.getComponent(
                    "properties", PropertiesComponent.class);
        }
        return propertiesComponent;
    }

    @Override
    public String lookupProperty(String s) throws Exception {
        //create the propertyprefix
        String prefix = this._dwa.getApplicationName() + "." + getConfiguration().getServiceName() +".";
        return getPropertiesComponent().parseUri(prefix + s);
    }

    @Override
    public Object lookupProperty(Class resultType, String uri)throws Exception{
            String value = lookupProperty(uri);
            return this.camelContext.getTypeConverter().convertTo(resultType, value);
    }

    @Override
    public <T> T safeLookupProperty(Class<T> resultType, String uri, T defaultIfUnsafe) {
        try {
            T result = (T)lookupProperty(resultType, uri);
            return result;
        } catch (Exception e) {
            return defaultIfUnsafe;
        }
    }

    @Override
    public void start() {
        try {
            this.getCamelContext().start();
            this.state = ServiceState.Up;
        } catch (Exception e) {
            throw new RuntimeException("Could not start service", e);
        }
    }

    @Override
    public void stop() {
        try {
            this.getCamelContext().stop();
            this.state = ServiceState.Stopped;
        } catch (Exception e) {
            throw new RuntimeException("could not stop service : ", e);
        }
    }

    public void configure(DrinkWaterApplication serviceRepository) throws Exception {

        //create tracing routes
        this.getCamelContext().addRoutes(createServiceTraceRoutes(getConfiguration().getIsTraceEnabled()));

        if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanObject) {
            //nothing to configure here
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.BeanClass) {
            this.camelContext.addRoutes(RouteBuilders.mapBeanClassRoutes(serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Rest) {
            this.camelContext.addRoutes(RouteBuilders.mapRestRoutes(serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Task) {
            this.camelContext.addRoutes(RouteBuilders.mapCronRoutes(this._dwa.getPropertiesDefaultName(), serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.Routeur) {
            this.camelContext.addRoutes(RouteBuilders.mapRoutingRoutes(serviceRepository, this));
        } else if (this.serviceConfiguration.getScheme() == ServiceScheme.HttpProxy) {
            this.camelContext.addRoutes(RouteBuilders.mapHttpProxyRoutes(serviceRepository, this));
        }

    }

    private RouteBuilder createServiceTraceRoutes(boolean isTracingEnabled) {

        return new TraceRouteBuilder(this, isTracingEnabled);
    }

    @Override
    public ServiceState getState() {
        return state;
    }

    @Override
    public String getApplicationName() {
        return _dwa.getApplicationName();
    }

    @Override
    public void sendEvent(Class eventClass, Method method, Object body) {
        if (eventClass.getName().equals(ClientReceivedEvent.class.getName())) {
            addClientReceivedTracing(this.camelContext, this, method, body);
        } else if (eventClass.getName().equals(ClientSentEvent.class.getName())) {
            addClientSentTracing(this.camelContext, this, method, body);
        }else {
            throw new RuntimeException("Event currently not coded");
        }
    }

    @Override
    public IServiceConfiguration getConfiguration() {
        return serviceConfiguration;
    }

    @Override
    public String toString() {
        return String.format("%s [%s : %s]",
                serviceConfiguration.getServiceName(),
                serviceConfiguration.getScheme(),
                serviceConfiguration.getServiceClass().getSimpleName());
    }


}
