package drinkwater.core;

import java.util.List;

/**
 * Created by A406775 on 29/12/2016.
 */
public interface IServiceConfiguration {

    Class getServiceClass();

    String getProperties();

    Class getTargetBeanClass();

    ServiceScheme getScheme();

    InjectionStrategy getInjectionStrategy();

    List<IServiceConfiguration> getServiceDependencies();

    String lookupProperty(String s) throws Exception;
}