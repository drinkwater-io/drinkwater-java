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
import java.util.List;

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
            String endpoint = endpointFrom(resolver, config);

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

        HttpRequest request = Unirest.get(endpoint + "/" + restPathFor(method));

        MethodToRestParameters params = new MethodToRestParameters(method);

        request = setAcceptHeader(method, request);

        request = request.header("Content-Type", "application/json");

        request = buildQueryStringFromHeaders(params.getHeaders(), args, 0, request);

        return executeRequest(method, request);
    }

    public static Object post(String endpoint, Method method, Object[] args) throws UnirestException {

        HttpRequestWithBody request = Unirest.post(endpoint + "/" + restPathFor(method));

        MethodToRestParameters params = new MethodToRestParameters(method);

        request = (HttpRequestWithBody) setAcceptHeader(method, request);

        request = (HttpRequestWithBody) buildQueryStringFromHeaders(params.getHeaders(), args, 1, request);

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

        HttpRequestWithBody request = Unirest.delete(endpoint + "/" + restPathFor(method));

        MethodToRestParameters params = new MethodToRestParameters(method);

        request = (HttpRequestWithBody) setAcceptHeader(method, request);

        request = (HttpRequestWithBody) buildQueryStringFromHeaders(params.getHeaders(), args, 1, request);

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


    private static HttpRequest buildQueryStringFromHeaders(
            List<String> headers,
            Object[] args,
            int startAt,
            HttpRequest request) {
        if (headers.size() > 0) {
            int i = startAt;
            for (String header : headers) {
                String headerValue = "";
                if (args[i] != null) {
                    if (!isPrimitiveOrString(args[i].getClass())) {
                        headerValue = new UnirestJacksonObjectMapper().writeValue(args[i]);
                    } else {
                        headerValue = args[i].toString();
                    }
                }
                request = request.queryString(header, headerValue);
                i++;
            }
        }
        return request;
    }


}
