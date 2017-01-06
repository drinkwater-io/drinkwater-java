package test.drinkwater.core.model.forTracing;

/**
 * Created by A406775 on 5/01/2017.
 */
public class ServiceBImpl implements IServiceB {

    IServiceC servicec;

    @Override
    public String getDataFromB() throws Exception {
        Thread.sleep(10);
        String result = "data from B - " + servicec.getDataFromC();
        Thread.sleep(10);

        return result;
    }
}
