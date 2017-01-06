package test.drinkwater.core.model.forTracing;

/**
 * Created by A406775 on 6/01/2017.
 */
public class ServiceDImpl implements IServiceD {
    @Override
    public String makeSomeComputation(String s) throws Exception {
        Thread.sleep(10);
        String result = s + " /computed in D/ ";
        Thread.sleep(10);

        return result;
    }
}
