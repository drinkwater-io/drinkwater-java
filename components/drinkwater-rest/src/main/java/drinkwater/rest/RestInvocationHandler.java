package drinkwater.rest;

import drinkwater.rest.Rest;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
