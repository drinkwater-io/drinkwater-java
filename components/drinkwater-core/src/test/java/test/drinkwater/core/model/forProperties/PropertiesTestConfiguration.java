package test.drinkwater.core.model.forProperties;

import drinkwater.ServiceConfigurationBuilder;
import test.drinkwater.core.model.ITestService;
import test.drinkwater.core.model.TestServiceImpl;

/**
 * Created by A406775 on 4/01/2017.
 */
public class PropertiesTestConfiguration extends ServiceConfigurationBuilder {

    private String propertiesFilePath;

    private String serviceName = "test-properties";

    public PropertiesTestConfiguration(){}

    public PropertiesTestConfiguration(String serviceName, String propertiesFilePath) {
        this.serviceName = serviceName;
        this.propertiesFilePath = propertiesFilePath;
    }

    @Override
    public void configure() {

        addService(serviceName, ITestService.class, new TestServiceImpl())
                .withProperties("file:" + propertiesFilePath).asRest();
    }
}
