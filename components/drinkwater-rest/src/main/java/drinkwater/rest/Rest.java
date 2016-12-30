package drinkwater.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.RequestBodyEntity;
import drinkwater.IPropertyResolver;
import drinkwater.IServiceConfiguration;

import java.lang.reflect.Method;

import static drinkwater.helper.StringHelper.trimEnclosingQuotes;
import static drinkwater.helper.reflect.ReflectHelper.isPrimitiveOrString;
import static drinkwater.helper.reflect.ReflectHelper.isString;
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
                case DELETE:
                    return delete(endpoint, method, args);
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

        MethodToRestParameters params = new MethodToRestParameters(method);

        HttpRequest request = Unirest.get(endpoint + "/" + restPathFor(method));

        request = setAcceptHeader(method, request);

        if (params.hasHeaders()) {
            int i = 0;
            for (String header : params.getHeaders()) {
                request = request.queryString(header, args[i]);
                i++;
            }
        }

        return executeRequest(method, request);
    }


    private static HttpRequest setAcceptHeader(Method method, HttpRequest request) {
        if (isPrimitiveOrString(returnTypeof(method))) {
            request = request.header("accept", "text/plain");

        } else {
            request = request.header("accept", "application/json");

        }
        return request;
    }

    private static Object extractBody(Method method, HttpResponse response) {
        if (isString(returnTypeof(method)) && response.getBody() != null) {
            return trimEnclosingQuotes(response.getBody().toString());
        } else {
            return response.getBody();
        }
    }


    private static Object executeRequest(Method method, HttpRequest request) throws UnirestException {

        HttpResponse response;

        if (isString(returnTypeof(method))) {
            response = request.asString();
        } else {
            response = request.asObject(returnTypeof(method));
        }

        return extractBody(method, response);
    }

    public static Object post(String endpoint, Method method, Object[] args) throws UnirestException {

        MethodToRestParameters params = new MethodToRestParameters(method);

        HttpRequestWithBody request = Unirest.post(endpoint + "/" + restPathFor(method));

        request = (HttpRequestWithBody) setAcceptHeader(method, request);

        HttpResponse<Object> result;

        if (params.hasHeaders()) {
            int i = 1;
            for (String header : params.getHeaders()) {
                request = request.queryString(header, args[i]);
                i++;
            }
        }

        //execute request
        if (params.hasBody()) {
            request = request.header("Content-Type", "application/json");
            RequestBodyEntity entity = request.body(args[0]);
            HttpResponse resp = entity.asObject(returnTypeof(method));
            return extractBody(method, resp);
        } else {
            return executeRequest(method, request); //request.body("").asObject(returnTypeof(method));
        }
    }

    public static Object delete(String endpoint, Method method, Object[] args) throws UnirestException {

        MethodToRestParameters params = new MethodToRestParameters(method);

        HttpRequestWithBody request = Unirest.delete(endpoint + "/" + restPathFor(method));

        request = (HttpRequestWithBody) setAcceptHeader(method, request);

        HttpResponse<Object> result;

        if (params.hasHeaders()) {
            int i = 1;
            for (String header : params.getHeaders()) {
                request = request.queryString(header, args[i]);
                i++;
            }
        }

        //execute request
        if (params.hasBody()) {
            request = request.header("Content-Type", "application/json");
            RequestBodyEntity entity = request.body(args[0]);
            HttpResponse resp = entity.asObject(returnTypeof(method));
            return extractBody(method, resp);
        } else {
            return executeRequest(method, request); //request.body("").asObject(returnTypeof(method));
        }
    }

}
