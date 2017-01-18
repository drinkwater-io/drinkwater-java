package drinkwater.helper.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

/**
 * Created by A406775 on 3/01/2017.
 */
public class CustomJacksonObjectMapper extends ObjectMapper {

    public CustomJacksonObjectMapper() {
        //TODo fix this issue on deprecetion
        //this.registerModule(new JSR310Module());
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.enable(SerializationFeature.INDENT_OUTPUT);
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public CustomJacksonObjectMapper(boolean indentation) {
        this();
        if (indentation) {
            this.enable(SerializationFeature.INDENT_OUTPUT);
        } else {
            this.disable(SerializationFeature.INDENT_OUTPUT);
        }
    }

}

