package drinkwater;

/**
 * Created by A406775 on 4/01/2017.
 */
public interface IServiceBuilder {

    IServiceBuilder forService(Class serviceClass);

    ServiceConfiguration name(String name);

    IServiceBuilder withProperties(String propertyFile);

    IServiceBuilder useBeanClass(Class bean);

    ServiceConfiguration useBean(Object bean);

    IServiceBuilder asRest();

    IServiceBuilder withInjectionStrategy(InjectionStrategy strategy);

    ServiceConfiguration dependsOn(String... services);
}
