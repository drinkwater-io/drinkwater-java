package test.drinkwater.core.model;

/**
 * Created by A406775 on 2/01/2017.
 */
public class TestServiceImpl implements ITestService {

    private String info = "test info";

    public TestServiceImpl() {
    }

    public TestServiceImpl(String info) {
        this.info = info;
    }

    @Override
    public String getInfo() {
        return info;
    }


}
