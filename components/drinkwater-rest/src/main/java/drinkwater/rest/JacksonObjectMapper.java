package drinkwater.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;

import java.io.IOException;

/**
 * Created by A406775 on 30/12/2016.
 */
public class JacksonObjectMapper implements ObjectMapper {
    private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
            = new com.fasterxml.jackson.databind.ObjectMapper();

    public <T> T readValue(String value, Class<T> valueType) {
        try {
            if (valueType.equals(Void.TYPE)) {
                return null;
            }
            return jacksonObjectMapper.readValue(value, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String writeValue(Object value) {
        try {
            String result = jacksonObjectMapper.writeValueAsString(value);
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
