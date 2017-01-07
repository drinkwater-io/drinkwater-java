package test.drinkwater.core.model.forRouting;

/**
 * Created by A406775 on 6/01/2017.
 */
public class TestRoutingImpl implements ITestRouting {

    String someServiceSpecificProperty;

    @Override
    public String getData() throws Exception {

        if (someServiceSpecificProperty == null) {
            return "property not set";
        }
        return someServiceSpecificProperty;
    }
}
