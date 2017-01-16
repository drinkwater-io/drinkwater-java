package drinkwater.test;

import com.mashape.unirest.http.HttpMethod;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by A406775 on 22/12/2016.
 */
public class HttpUnitTest {

    public static HttpTestRequest httpGet(String request, Class objectType, Map<String, String> headers) {
        return new HttpTestRequest(HttpMethod.GET, request, null, HttpTestRequest.ResponseType.Object, objectType, headers);
    }

    public static HttpTestRequest httpGet(String request) {
        return new HttpTestRequest(HttpMethod.GET, request, null, HttpTestRequest.ResponseType.Json);
    }

    public static HttpTestRequest httpGetString(String request) {
        return new HttpTestRequest(HttpMethod.GET, request, null, HttpTestRequest.ResponseType.String);
    }

    public static HttpTestRequest httpGetString(String request, Map<String, String> headers) {
        return new HttpTestRequest(HttpMethod.GET, request, null, HttpTestRequest.ResponseType.String, null, headers);
    }

    public static HttpTestRequest httpPost(String request, String body) {
        return new HttpTestRequest(HttpMethod.POST, request, body, HttpTestRequest.ResponseType.Json);
    }

    public static HttpTestRequest httpPost(String request, String body,  Class responseType, Map<String, String> headers) {
        return new HttpTestRequest(HttpMethod.POST, request, body, HttpTestRequest.ResponseType.Object, responseType, headers);
    }

    public static HttpTestRequest httpPostFile(String request, InputStream is, Class responseType, Map<String, String> headers) {
        return new HttpTestRequest(HttpMethod.POST, request, is, HttpTestRequest.ResponseType.Object, responseType, headers);
    }

    public static HttpTestRequest httpPostRequestString(String request, String body) {
        return new HttpTestRequest(HttpMethod.POST, request, body, HttpTestRequest.ResponseType.String);
    }

    public static HttpTestRequest httpPut(String request, String body) {
        return new HttpTestRequest(HttpMethod.PUT, request, body, HttpTestRequest.ResponseType.Json);
    }

    public static HttpTestRequest httpPut(String request, String body,  Class responseType, Map<String, String> headers) {
        return new HttpTestRequest(HttpMethod.PUT, request, body, HttpTestRequest.ResponseType.Object, responseType, headers);
    }

    public static HttpTestRequest httpDelete(String request) {
        return new HttpTestRequest(HttpMethod.DELETE, request, null, HttpTestRequest.ResponseType.String);
    }


}
