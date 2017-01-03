package drinkwater.helper.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by A406775 on 3/01/2017.
 */
public class CustomJacksonObjectMapper extends ObjectMapper {

    public CustomJacksonObjectMapper() {
        this.findAndRegisterModules();
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.enable(SerializationFeature.INDENT_OUTPUT);
    }

}

