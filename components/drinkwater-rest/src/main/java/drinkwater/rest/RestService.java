package drinkwater.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
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
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void stop() {
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
