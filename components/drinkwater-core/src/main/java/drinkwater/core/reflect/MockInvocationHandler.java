package drinkwater.core.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by A406775 on 3/01/2017.
 */
public class MockInvocationHandler implements InvocationHandler {

    private final Object target;

    //FIXME : review how the invocation route done for direct object access.
    public MockInvocationHandler(Object bean) {
        try {
            this.target = bean;
        } catch (Exception e) {
            throw new RuntimeException("could not create bean");
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        return method.invoke(target, args);
    }
}

