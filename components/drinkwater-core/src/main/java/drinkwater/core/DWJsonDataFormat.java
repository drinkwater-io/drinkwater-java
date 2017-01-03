package drinkwater.core;

import drinkwater.helper.json.CustomJacksonObjectMapper;
import org.apache.camel.component.jackson.JacksonDataFormat;

/**
 * Created by A406775 on 3/01/2017.
 */
public class DWJsonDataFormat extends JacksonDataFormat {

    public DWJsonDataFormat() {
        setObjectMapper(new CustomJacksonObjectMapper());
    }

}
