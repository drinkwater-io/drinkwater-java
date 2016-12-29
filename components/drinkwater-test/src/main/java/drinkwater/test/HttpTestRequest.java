package drinkwater.test;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.javacrumbs.jsonunit.core.Option;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.when;
import static org.junit.Assert.assertEquals;

/**
 * Created by A406775 on 22/12/2016.
 */
public class HttpTestRequest {

    private String request;

    private HttpResponse<JsonNode> response;

    private HttpResponse<String> responseString;

    public HttpTestRequest(HttpMethod method, String request, String body){
        this.request = request;

        try {
            if(method == HttpMethod.GET) {
                response = Unirest.get(request).asJson();
            }
            else if (method == HttpMethod.POST){
                responseString = Unirest.post(request)
                        //.header("accept", "application/json")
                        .header("accept", "text/plain")
                        .header("Content-Type", "application/json")
                        .body(rs(body))
                        .asString();
            }
            else if (method == HttpMethod.PUT){
                response = Unirest.put(request)
                        .body(body)
                        .asJson();
            }
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public HttpTestRequest expectsBody(String expected) throws UnirestException {
        if(responseString != null){
            assertEquals(expected, responseString.getBody());
        }
        else {
            assertJsonEquals(rs(expected), response.getBody().toString(), when(Option.IGNORING_ARRAY_ORDER));
        }
        return this;
    }

    public HttpTestRequest expectsStatus(int statusCode) throws UnirestException {
        assertEquals(response.getStatus() ,statusCode);
        return this;
    }

    private String rs(String s){
        String answer = s.replaceAll("'","\"");
        return answer;
    }


}
