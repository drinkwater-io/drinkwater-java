package drinkwater.rest;

import org.apache.camel.CamelContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by A406775 on 29/12/2016.
 */

public class RestInvocationHandler implements InvocationHandler {

    private final CamelContext context;

    public RestInvocationHandler(CamelContext context) {
        this.context = context;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return Rest.invoke(proxy, method, args);
    }
}
