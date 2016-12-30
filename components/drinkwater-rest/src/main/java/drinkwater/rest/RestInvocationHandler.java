package drinkwater.rest;

import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by A406775 on 29/12/2016.
 */

public class RestInvocationHandler implements InvocationHandler {

    IPropertyResolver resolver;
    IServiceConfiguration config;

    public RestInvocationHandler(IPropertyResolver resolver, IServiceConfiguration config) {

        this.config = config;
        this.resolver = resolver;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return Rest.invoke(proxy, method, args, resolver, config);
    }
}
