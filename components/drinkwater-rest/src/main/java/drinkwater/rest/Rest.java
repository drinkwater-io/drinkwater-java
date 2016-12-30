package drinkwater.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;

import java.lang.reflect.Method;

import static drinkwater.rest.RestHelper.*;

/**
 * Created by A406775 on 30/12/2016.
 */
public final class Rest {

    public static Object invoke(Object obj, Method method, Object[] args, IPropertyResolver resolver, IServiceConfiguration config) {
        Object result = null;

        try {
            String endpoint = "http://" + host(resolver) + ":" + port(resolver) + "/" + context(resolver, config);

            switch (httpMethodFor(method)) {
                case GET:
                    return get(endpoint, method, args);
                case POST:
                    return post(endpoint, method, args);
                default:
                    throw new RuntimeException(String.format("Could not map method %s to the corresponding httpVerb", method.getName()));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class returnTypeof(Method method) {
        return method.getReturnType();
    }

    public static Object get(String endpoint, Method method, Object[] args) throws UnirestException {

        Object result = Unirest.get(endpoint + restPathFor(method))
                .asObject(returnTypeof(method));

        return result;
    }

    public static Object post(String endpoint, Method method, Object[] args) throws UnirestException {

        HttpResponse<Object> result =
                Unirest.post(endpoint + "/" + restPathFor(method))
                        .queryString("volume", args[1])
                        .body(args[0])
                        .asObject(returnTypeof(method));

        return result.getBody();
    }
}
