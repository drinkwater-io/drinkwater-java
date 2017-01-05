package drinkwater.rest;

import drinkwater.IDrinkWaterService;
import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;
import drinkwater.trace.ServerReceivedEvent;
import drinkwater.trace.ServerSentEvent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by A406775 on 29/12/2016.
 */

public class RestInvocationHandler implements InvocationHandler {

    IPropertyResolver resolver;
    IDrinkWaterService service;
    IServiceConfiguration configuration;

    public RestInvocationHandler(IPropertyResolver resolver, IDrinkWaterService service) {

        this.configuration = service.getConfiguration();
        this.service = service;
        this.resolver = resolver;
    }

    //fixme make it thread safe another way. the probleme here is the configuration...
    @Override
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invocationResult = null;
        String description = String.format("%s.%s", configuration.getServiceName(), method.getName());
        String correlationId = UUID.randomUUID().toString();

        try {
            service.sendEvent(new ServerSentEvent(correlationId, description, method, args));

            invocationResult = Rest.invoke(proxy, method, args, resolver, configuration);
        } finally {
            service.sendEvent(new ServerReceivedEvent(correlationId, description, method, args, invocationResult));
        }

        return invocationResult;
    }
}
