package drinkwater.test;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import net.javacrumbs.jsonunit.core.Option;

import static drinkwater.helper.StringHelper.trimEnclosingQuotes;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.when;
import static org.junit.Assert.assertEquals;

/**
 * Created by A406775 on 22/12/2016.
 */
public class HttpTestRequest {

    public enum ResponseType {Json, String}

    private String request;

    private ResponseType responseType;

    private HttpResponse<JsonNode> jsonResponse;

    private HttpResponse<String> stringResponse;

    public HttpTestRequest(HttpMethod method, String request, String body, ResponseType responseType) {
        this.request = request;
        this.responseType = responseType;

        try {
            if (method == HttpMethod.GET) {
                if (responseType == ResponseType.Json) {
                    jsonResponse = Unirest.get(request).asJson();
                } else {
                    stringResponse = Unirest.get(request).asString();
                }
            } else if (method == HttpMethod.POST) {

                HttpRequestWithBody postRequest = Unirest.post(request)
                        .header("Content-Type", "application/json");

                if (responseType == ResponseType.String) {
                    postRequest = postRequest.header("accept", "text/plain");
                    stringResponse = postRequest.body(rs(body)).asString();
                } else if (responseType == ResponseType.Json) {
                    postRequest = postRequest.header("accept", "application/json");
                    jsonResponse = postRequest.body(rs(body)).asJson();
                }

            } else if (method == HttpMethod.PUT) {
                jsonResponse = Unirest.put(request)
                        .body(body)
                        .asJson();
            }
        } catch (Exception ex) {
            throw new RuntimeException("error while issuing request : " + request, ex);
        }
    }

    public HttpTestRequest expectsBody(String expected) throws UnirestException {
        if (responseType == ResponseType.String) {
            assertEquals(expected, trimEnclosingQuotes(stringResponse.getBody()));
        } else {
            assertJsonEquals(rs(expected), jsonResponse.getBody().toString(), when(Option.IGNORING_ARRAY_ORDER));
        }
        return this;
    }

    public HttpTestRequest expectsStatus(int statusCode) throws UnirestException {
        if (responseType == ResponseType.String) {
            assertEquals(stringResponse.getStatus(), statusCode);
        } else {
            assertEquals(jsonResponse.getStatus(), statusCode);
        }

        return this;
    }

    private String rs(String s) {
        String answer = s.replaceAll("'", "\"");
        return answer;
    }


}
