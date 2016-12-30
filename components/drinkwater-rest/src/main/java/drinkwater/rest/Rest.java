package drinkwater.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.lang.reflect.Method;

import static drinkwater.rest.RestHelper.httpMethodFor;
import static drinkwater.rest.RestHelper.restPathFor;

/**
 * Created by A406775 on 30/12/2016.
 */
public final class Rest {

    public static Object invoke(Object obj, Method method, Object[] args) {
        Object result = null;
        //fixme get it from config
        String endpoint = "http://localhost:8889/idrinktrackerservice";

        try {
            switch (httpMethodFor(method)) {
                case GET:
                    return get(endpoint, method, args);
                case POST:
                    return post(endpoint, method, args);
                default:
                    throw new RuntimeException(String.format("Could not map method %s to the corresponding httpVerb", method.getName()));
            }

        } catch (UnirestException e) {
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
