package drinkwater.rest;

import drinkwater.IDrinkWaterService;
import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;
import drinkwater.trace.ClientReceivedEvent;
import drinkwater.trace.ClientSentEvent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by A406775 on 29/12/2016.
 */

public class RestInvocationHandler implements InvocationHandler {

    private final IPropertyResolver resolver;
    private final IDrinkWaterService service;
    private final IServiceConfiguration configuration;
//    private final Function3<Class, Method, Object, Boolean> sendEventFunc;

    public RestInvocationHandler(IPropertyResolver resolver, IDrinkWaterService service) {

        this.configuration = service.getConfiguration();
        this.service = service;
        this.resolver = resolver;
//        this.sendEventFunc = (clazz, method, body) ->
//                service.sendEvent(clazz, method, body);
    }

    //fixme make it thread safe another way. the probleme here route the configuration...
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        service.sendEvent(ClientReceivedEvent.class, method, args);
        Object invocationResult = null;
        try {
            invocationResult = Rest.invoke(proxy, method, args, resolver, configuration);
        } finally {
            service.sendEvent(ClientSentEvent.class, method, invocationResult);
        }

        return invocationResult;
    }
}
