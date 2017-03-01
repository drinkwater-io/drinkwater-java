package drinkwater.rest.converters;

import drinkwater.rest.UnirestJacksonObjectMapper;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.FallbackConverter;
import org.apache.camel.spi.TypeConverterRegistry;

/**
 * Created by A406775 on 30/12/2016.
 */
@Converter
public class CustomCamelConverters {

    @FallbackConverter
    public static <T> T convertTo(Class<T> type, Exchange exchange, Object value, TypeConverterRegistry registry) {

        try {
            if (value != null && value.getClass().equals(String.class)) {
                if (value.toString().startsWith("{") || value.toString().startsWith("[")) {
                    T result = new UnirestJacksonObjectMapper().readValue(value.toString(), type);

                    return result;
                }
            }
        }
        catch(Exception ex){
            return null;
        }
        return null;
    }
}
