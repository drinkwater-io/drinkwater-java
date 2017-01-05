package test.drinkwater.core.model.forTracing;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceBImpl implements IServiceB {
    @Override
    public String getDataFromB() {
        return "hello";
    }
}
