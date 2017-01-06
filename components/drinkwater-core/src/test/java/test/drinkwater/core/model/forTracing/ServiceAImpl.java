package test.drinkwater.core.model.forTracing;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceAImpl implements IServiceA {

    IServiceB serviceB;

    IServiceD serviceD;

    @Override
    public String getDataFromA() throws Exception {
        Thread.sleep(10);

        String result = serviceD.makeSomeComputation("data from A - " + serviceB.getDataFromB());

        Thread.sleep(10);

        return result;
    }
}
