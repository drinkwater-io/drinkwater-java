package drinkwater;

import java.util.List;
import java.util.Map;

/**
 * Created by A406775 on 29/12/2016.
 */
public interface IServiceConfiguration extends IPropertiesAware{

    Class getServiceClass();



    Class getTargetBeanClass();

    ServiceScheme getScheme();

    void setScheme(ServiceScheme beanObject);

    InjectionStrategy getInjectionStrategy();

    void setInjectionStrategy(InjectionStrategy none);

    List<String> getServiceDependencies();

    Object getTargetBean();

    void setTargetBean(Object beanObject);

    String getServiceName();

    IServiceConfiguration patchWith(IServiceConfiguration patchConfig);

    String getCronExpression();

    int getRepeatInterval();

    Boolean getIsTraceEnabled();

    void setIsTraceEnabled(Boolean traceEvent);

    String getRoutingHeader();

    Map<String, String> getRoutingMap();

    String getServiceHost();

    void setServiceHost(String host);


}