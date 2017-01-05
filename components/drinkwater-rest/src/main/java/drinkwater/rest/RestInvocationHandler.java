package drinkwater.rest;

import drinkwater.IDrinkWaterService;
import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;
import drinkwater.trace.Payload;
import drinkwater.trace.ServerReceivedEvent;
import drinkwater.trace.ServerSentEvent;
import org.apache.camel.ProducerTemplate;

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
    ProducerTemplate template;

    public RestInvocationHandler(IPropertyResolver resolver, IDrinkWaterService service, ProducerTemplate template) {

        this.configuration = service.getConfiguration();
        this.service = service;
        this.resolver = resolver;
        this.template = template;
    }

    //fixme make it thread safe another way. the probleme here is the configuration...
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String correlationId = UUID.randomUUID().toString();
        template.sendBody("vm:trace", new ServerSentEvent(correlationId, "desc", Payload.of(method, args)));
        Object invocationResult = null;
        try {
            invocationResult = Rest.invoke(proxy, method, args, resolver, configuration);
        } finally {
            template.sendBody("vm:trace", new ServerReceivedEvent(correlationId, "", Payload.of(method, args, invocationResult)));
        }

        return invocationResult;
    }
}
