package drinkwater.helper.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

/**
 * Created by A406775 on 3/01/2017.
 */
public class CustomJacksonObjectMapper extends ObjectMapper {

    public CustomJacksonObjectMapper() {
        this.registerModule(new JSR310Module());
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.enable(SerializationFeature.INDENT_OUTPUT);
    }

}

