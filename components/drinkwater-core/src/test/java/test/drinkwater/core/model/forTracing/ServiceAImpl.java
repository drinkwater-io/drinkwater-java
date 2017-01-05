package test.drinkwater.core.model.forTracing;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceAImpl implements IServiceA {

    IServiceB serviceB;

    @Override
    public String getDataFromA() {
        return serviceB.getDataFromB();
    }
}
