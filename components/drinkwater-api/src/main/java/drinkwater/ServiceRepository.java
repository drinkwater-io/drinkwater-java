package drinkwater;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface ServiceRepository extends IPropertyResolver {
    <T> T getService(Class<? extends T> iface);

    <T> T getService(String serviceName);

    IServiceConfiguration getServiceDefinition(String serviceName);

    public <T> T getStore(String name) ;

}
