package drinkwater.rest;

import drinkwater.IDrinkWaterService;
import drinkwater.IPropertyResolver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by A406775 on 29/12/2016.
 */

public class RestInvocationHandler implements InvocationHandler {

    IPropertyResolver resolver;
    IDrinkWaterService service;

    public RestInvocationHandler(IPropertyResolver resolver, IDrinkWaterService service) {

        this.service = service;
        this.resolver = resolver;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        return Rest.invoke(proxy, method, args, resolver, service.getConfiguration());
    }
}
