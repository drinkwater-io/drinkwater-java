package drinkwater.test;

import com.mashape.unirest.http.HttpMethod;

/**
 * Created by A406775 on 22/12/2016.
 */
public class HttpUnitTest {

    public static HttpTestRequest httpGet(String request) {
        return new HttpTestRequest(HttpMethod.GET, request, null, HttpTestRequest.ResponseType.Json);
    }

    public static HttpTestRequest httpGetString(String request) {
        return new HttpTestRequest(HttpMethod.GET, request, null, HttpTestRequest.ResponseType.String);
    }

    public static HttpTestRequest httpPost(String request, String body) {
        return new HttpTestRequest(HttpMethod.POST, request, body, HttpTestRequest.ResponseType.Json);
    }

    public static HttpTestRequest httpPostRequestString(String request, String body) {
        return new HttpTestRequest(HttpMethod.POST, request, body, HttpTestRequest.ResponseType.String);
    }

    public static HttpTestRequest httpPut(String request, String body) {
        return new HttpTestRequest(HttpMethod.PUT, request, body, HttpTestRequest.ResponseType.Json);
    }
}
