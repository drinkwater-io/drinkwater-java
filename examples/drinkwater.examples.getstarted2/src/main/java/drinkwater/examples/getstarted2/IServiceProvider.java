package drinkwater.examples.getstarted2;

public interface IServiceProvider {

    <T> T getService(Class serviceType) throws Exception;
}
