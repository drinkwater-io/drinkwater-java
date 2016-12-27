package drinkwater.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javaslang.collection.List;
import drinkwater.core.rest.RestRouteBuilderFactory;
import org.apache.camel.cdi.CdiCamelContext;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withReturnType;

@ApplicationScoped
@ContextName("MASTER")
public class MasterContext extends CdiCamelContext {

    @Inject
    @DrinkWaterApplicationConfig
    Instance<Object> applicationConfig;

//    @Inject
//    private DefaultPropertiesResolver propertyResolver;

    @Inject
    RestRouteBuilderFactory restRouteBuilder;

//    @Inject
//    MasterContextLifecycle lifeCycleHandler;

    java.util.List<Service> _services = new ArrayList<>();

    @PostConstruct
    void customize() {
        // Disable JMX
        disableJMX();
        // Set the Camel context name
        setStreamCaching(true);
        setName("Master");
        PropertiesComponent prop = this.getComponent(
                "properties", PropertiesComponent.class);
        prop.setLocation("classpath:drinkwater.properties");

//        Object startegies = getLifecycleStrategies();
//
//        addLifecycleStrategy(lifeCycleHandler);
//
//        startegies = getLifecycleStrategies();

        List<ServiceConfiguration> _result = getServiceConfigurations();

        for (ServiceConfiguration config : _result){

            createServiceFromConfig(this, config);
        }
        //result.forEach(this::createServiceFromConfig);
    }


    @Produces
    public Service getConfig(InjectionPoint ip){
        return null;
    }

    @Produces
    @Named("json-jackson")
    public JacksonDataFormat getjacksonDataFormat() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JacksonDataFormat formatter = new JacksonDataFormat(mapper, (Class)null);

        return formatter;
    }

    @PreDestroy
    void cleanUp() {
        //kill the contained context before
    }


    public void createServiceFromConfig(DefaultCamelContext masterContext, ServiceConfiguration config){
        try {
            Service svc = new Service();
            DefaultCamelContext ctx = new DefaultCamelContext();
            svc.setContext(ctx);
            PropertiesComponent prop = ctx.getComponent(
                    "properties", PropertiesComponent.class);
            prop.setLocation(config.getPropertiesFileLocation());
//            Properties props = propertyResolver.resolveProperties(ctx, false, config.getPropertiesFileLocation());
//            svc.setProperties(props);
            _services.add(svc);
            ctx.disableJMX();
            ctx.setName(config.getContextPath());
            //configure ef service => should select using service name
            ctx.addRoutes(restRouteBuilder.createRestRouteBuilder(config));
            ctx.start();
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<ServiceConfiguration> getServiceConfigurations(){
        Object applicationConfigObject =  applicationConfig.get();

        DrinkWaterApplicationConfig configAnnotation = applicationConfigObject.getClass().getAnnotation(DrinkWaterApplicationConfig.class);

        java.util.Set<Method> methods = getMethods(applicationConfigObject.getClass(),
                withReturnType(ServiceConfigurationCollection.class));

        if(methods.size() == 0){
            throw new RuntimeException(String.format("The type annotated with @DrinkWaterApplicationConfig, " +
                            "should provide a method with signature public %s %s()", configAnnotation.configurationMethod(),
                    ServiceConfiguration.class.getName())
            );
        }

        List<ServiceConfiguration> configs = List.empty();

        configs = List.ofAll(methods).
                map(
                        m -> {
                            try {
                                return (ServiceConfigurationCollection)m.invoke(applicationConfigObject);
                            } catch (Exception e) {
                                throw new RuntimeException(String.format(
                                        "Error while configuring Application. call to method %s.%s failed", m.getDeclaringClass(),m.getName()));
                            }
                        })
                .flatMap(col -> col.getConfigurations());

        return configs;
    }

}