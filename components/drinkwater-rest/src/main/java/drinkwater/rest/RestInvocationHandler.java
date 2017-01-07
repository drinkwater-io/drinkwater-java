package drinkwater.rest;

import drinkwater.IDrinkWaterService;
import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;
import drinkwater.trace.ClientReceivedEvent;
import drinkwater.trace.ClientSentEvent;
import drinkwater.trace.Payload;
import javaslang.Function3;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by A406775 on 29/12/2016.
 */

public class RestInvocationHandler implements InvocationHandler {

    private final IPropertyResolver resolver;
    private final IDrinkWaterService service;
    private final IServiceConfiguration configuration;
    private final Function3<Class, Method, Payload, Boolean> sendEventFunc;

    public RestInvocationHandler(IPropertyResolver resolver, IDrinkWaterService service) {

        this.configuration = service.getConfiguration();
        this.service = service;
        this.resolver = resolver;
        this.sendEventFunc = (clazz, method, payload) ->
                service.sendEvent(clazz, method, payload);
    }

    //fixme make it thread safe another way. the probleme here route the configuration...
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        sendEventFunc.apply(ClientSentEvent.class, method, Payload.of(method, args));
        Object invocationResult = null;
        try {
            invocationResult = Rest.invoke(proxy, method, args, resolver, configuration);
        } finally {
            sendEventFunc.apply(ClientReceivedEvent.class, method, Payload.of(invocationResult));
        }

        return invocationResult;
    }
}
