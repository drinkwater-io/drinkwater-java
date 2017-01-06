package test.drinkwater.core.model.forTracing;

/**
 * Created by A406775 on 6/01/2017.
 */
public class ServiceCImpl implements IServiceC {
    @Override
    public String getDataFromC() throws Exception {
        Thread.sleep(10);
        String result = "data from c";
        Thread.sleep(10);

        return result;
    }
}
