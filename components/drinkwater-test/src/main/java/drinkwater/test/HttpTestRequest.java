package drinkwater.test;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.entity.ContentType;

import java.io.InputStream;
import java.util.Map;

import static drinkwater.helper.StringHelper.trimEnclosingQuotes;
import static drinkwater.test.TestHelper.rs;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.when;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.junit.Assert.assertEquals;

/**
 * Created by A406775 on 22/12/2016.
 */
public class HttpTestRequest {

    private Class objectType;
    private String request;
    private ResponseType responseType;
    private HttpResponse<JsonNode> jsonResponse;
    private HttpResponse<String> stringResponse;
    private HttpResponse<Class<?>> objectResponse;

    public HttpTestRequest(HttpMethod method, String request, String body, ResponseType responseType) {
        this(method, request, body, responseType, null, null);
    }


//    public HttpTestRequest(HttpMethod method, String request, InputStream body, ResponseType responseType) {
//        this(method, request, body, responseType, null, null);
//    }


    //TODO : refactor this spagethi
    public HttpTestRequest(HttpMethod method, String request, Object body, ResponseType responseType, Class objectType, Map<String, String> headers) {
        this.request = request;
        this.responseType = responseType;
        this.objectType = objectType;
        //TODO add option to disable timeout (for now socketTimeout is disabled)
        int disabledSocketTimeOut = 0;
        int connectionTimeOut = 10000;
        Unirest.setTimeouts(connectionTimeOut, disabledSocketTimeOut);

        try {
            if (method == HttpMethod.GET) {
                GetRequest getrequest = Unirest.get(request);

                if (headers != null) {
                    headers.forEach((key, value) -> getrequest.header(key, value));
                }

                if (responseType == ResponseType.Json) {
                    jsonResponse = getrequest.asJson();
                } else if (responseType == ResponseType.Object) {
                    objectResponse = getrequest.asObject(objectType);
                } else {
                    getrequest.header("accept", "text/plain");
                    stringResponse = getrequest.asString();
                }

            } else if (method == HttpMethod.POST) {

                HttpRequestWithBody postRequest = Unirest.post(request);

                if (body instanceof InputStream) {
                    objectResponse = postRequest.header("accept", "application/json")
                            .field("file", (InputStream) body, ContentType.APPLICATION_OCTET_STREAM, "testFile")
                            .asObject(objectType);
                } else if (body instanceof String) {
                    if (responseType == ResponseType.String) {
                        postRequest = postRequest.header("accept", "text/plain");
                        stringResponse = postRequest.body(rs((String) body)).asString();
                    } else if (responseType == ResponseType.Json) {
                        postRequest = postRequest.header("accept", "application/json");
                        postRequest = postRequest.header("Content-Type", "application/json");
                        jsonResponse = postRequest.body(rs((String) body)).asJson();
                    }
                    else if (responseType == ResponseType.Object) {
                        postRequest = postRequest.header("accept", "application/json");
                        postRequest = postRequest.header("Content-Type", "application/json");
                        objectResponse = postRequest.body(rs((String) body)).asObject(objectType);
                    }
                }

            } else if (method == HttpMethod.PUT) {

                HttpRequestWithBody putRequest = Unirest.put(request);

                if (responseType == ResponseType.String) {
                    putRequest = putRequest.header("accept", "text/plain");
                    stringResponse = putRequest.body(rs((String) body)).asString();
                } else if (responseType == ResponseType.Json) {
                    putRequest = putRequest.header("accept", "application/json");
                    putRequest = putRequest.header("Content-Type", "application/json");
                    jsonResponse = putRequest.body(rs((String) body)).asJson();
                }
                else if (responseType == ResponseType.Object) {
                    putRequest = putRequest.header("accept", "application/json");
                    putRequest = putRequest.header("Content-Type", "application/json");
                    objectResponse = putRequest.body(rs((String) body)).asObject(objectType);
                }

            }

            else if (method == HttpMethod.DELETE) {

                HttpRequestWithBody deleteRequest = Unirest.delete(request);

                if(body == null){
                    body = "";
                }

                if (responseType == ResponseType.String) {
                    deleteRequest = deleteRequest.header("accept", "text/plain");
                    stringResponse = deleteRequest.body(rs((String) body)).asString();
                } else if (responseType == ResponseType.Json) {
                    deleteRequest = deleteRequest.header("accept", "application/json");
                    deleteRequest = deleteRequest.header("Content-Type", "application/json");
                    jsonResponse = deleteRequest.body(rs((String) body)).asJson();
                }
                else if (responseType == ResponseType.Object) {
                    deleteRequest = deleteRequest.header("accept", "application/json");
                    deleteRequest = deleteRequest.header("Content-Type", "application/json");
                    objectResponse = deleteRequest.body(rs((String) body)).asObject(objectType);
                }

            }
        } catch (Exception ex) {
            throw new RuntimeException("error while issuing request : " + request, ex);
        }
    }

    public <T> T asObject() {
        if (responseType == ResponseType.String || responseType == ResponseType.Json) {
            return (T) result();
        } else {
            if(objectResponse == null){
                return null;
            }
            Object response = objectResponse.getBody();
            return (T) response;
        }
    }

    public String result() {
        if (responseType == ResponseType.String) {
            return trimEnclosingQuotes(stringResponse.getBody());
        } else {
            return jsonResponse.getBody().toString();
        }
    }

    public HttpTestRequest expectsBody(String expected) throws UnirestException {
        if (responseType == ResponseType.String) {
            assertEquals(expected, trimEnclosingQuotes(stringResponse.getBody()));
        } else {
            assertJsonEquals(rs(expected), jsonResponse.getBody().toString(), when(IGNORING_ARRAY_ORDER));
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

    public enum ResponseType {Json, String, Object}


}
