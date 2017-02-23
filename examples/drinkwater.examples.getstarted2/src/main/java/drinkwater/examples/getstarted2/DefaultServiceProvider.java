package drinkwater.examples.getstarted2;


import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.helper.BeanFactory;

public class DefaultServiceProvider implements IServiceProvider {

    DrinkWaterApplication app;

    public DefaultServiceProvider(DrinkWaterApplication app) {
        this.app = app;
    }

    @Override
    public <T> T getService(Class serviceType) throws Exception {
        return (T) BeanFactory.createBean(app, null, app);
    }
}
