package drinkwater.core.reflect;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.helper.Service;
import org.apache.camel.CamelContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static drinkwater.core.helper.BeanFactory.createBeanObject;

/**
 * Created by A406775 on 30/12/2016.
 */
public class BeanInvocationHandler implements InvocationHandler {

    private final CamelContext context;
    private final Object target;
    DrinkWaterApplication app;

    //FIXME : review how the invocation route done for direct object access.
    public BeanInvocationHandler(CamelContext context, DrinkWaterApplication app, Service service) {
        this.context = context;
        this.app = app;
        try {
            this.target = createBeanObject(app, service.getConfiguration(), service);
        } catch (Exception e) {
            throw new RuntimeException("could not create bean");
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target, args);
    }
}
