package drinkwater;

import java.util.List;

/**
 * Created by A406775 on 29/12/2016.
 */
public interface IServiceConfiguration {

    Class getServiceClass();

    String[] getProperties();

    Class getTargetBeanClass();

    ServiceScheme getScheme();

    void setScheme(ServiceScheme beanObject);

    InjectionStrategy getInjectionStrategy();

    void setInjectionStrategy(InjectionStrategy none);

    List<String> getServiceDependencies();

    Object getTargetBean();

    void setTargetBean(Object beanObject);

    String getServiceName();
}