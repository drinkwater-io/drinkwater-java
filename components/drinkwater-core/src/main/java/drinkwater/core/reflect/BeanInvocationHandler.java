package drinkwater.core.reflect;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.helper.InternalServiceConfiguration;
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

    //FIXME : review how the invocation is done for direct object access.
    public BeanInvocationHandler(CamelContext context, DrinkWaterApplication app, InternalServiceConfiguration config) {
        this.context = context;
        try {
            this.target = createBeanObject(app, config);
        } catch (Exception e) {
            throw new RuntimeException("could not create bean");
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        return method.invoke(target, args);
    }
}
