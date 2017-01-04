package drinkwater.rest;

import com.mashape.unirest.http.Unirest;

import java.io.IOException;

/**
 * Created by A406775 on 30/12/2016.
 */
public class RestService {

    public static final String REST_HOST_KEY = "drinkwater.rest.host";
    public static final String REST_PORT_KEY = "drinkwater.rest.port";
    public static final String REST_CONTEXT_KEY = "drinkwater.rest.contextpath";


    public void start() {
        Unirest.setObjectMapper(new UnirestJacksonObjectMapper());
    }

    public void stop() {
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
