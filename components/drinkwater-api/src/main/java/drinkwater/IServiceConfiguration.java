package drinkwater;

import java.util.List;

/**
 * Created by A406775 on 29/12/2016.
 */
public interface IServiceConfiguration {

    Class getServiceClass();

    String[] getPropertiesLocations();

    Class getTargetBeanClass();

    ServiceScheme getScheme();

    InjectionStrategy getInjectionStrategy();

    List<String> getServiceDependencies();

    Object getTargetBean();

    String getServiceName();

}