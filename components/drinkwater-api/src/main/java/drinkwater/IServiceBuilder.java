package drinkwater;

/**
 * Created by A406775 on 4/01/2017.
 */
public interface IServiceBuilder {

    IServiceBuilder forService(Class serviceClass);

    IServiceBuilder useTracing(Boolean traceEvent);

    ServiceConfiguration name(String name);

    IServiceBuilder withProperties(String propertyFile);

    IServiceBuilder useBeanClass(Class bean);

    ServiceConfiguration useBean(Object bean);

    IServiceBuilder asRest();

    IServiceBuilder asRemote();

    IServiceBuilder withInjectionStrategy(InjectionStrategy strategy);

    IServiceBuilder withProperty(String key, Object value);

    ServiceConfiguration dependsOn(String... services);

    IServiceBuilder cron(String cronExpression);

    IServiceBuilder repeat(int repeatInterval);
}
