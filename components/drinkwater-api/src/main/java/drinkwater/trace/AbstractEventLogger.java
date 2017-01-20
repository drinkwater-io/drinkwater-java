package drinkwater.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import drinkwater.IBaseEventLogger;
import drinkwater.helper.json.CustomJacksonObjectMapper;

import java.lang.reflect.Method;
import java.util.Map;

public abstract class AbstractEventLogger implements IBaseEventLogger {

    protected String serializeObject(Object obj) throws JsonProcessingException {


        final StringBuilder builder = new StringBuilder();
        if (obj instanceof Operation) {
            builder.append(obj.toString());
        }
        if (obj instanceof Method) {
            builder.append(((Method) obj).getName());
        } else if (obj instanceof String) {
            builder.append(obj);
        } else if (obj instanceof Map) {
            ((Map) obj).keySet().forEach(
                    key -> builder.append(" (" + key + " --> " + ((Map) obj).get(key) + ")"));

        } else {
            CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper(false);
            builder.append(mapper.writeValueAsString(obj));
        }

        return builder.toString();
    }

    protected String serializeEvent(BaseEvent event) throws JsonProcessingException {
        StringBuilder builder = new StringBuilder();
        builder.append(event.getTime().toEpochMilli());
        builder.append("-");
        builder.append(event.getName());
        builder.append("-");
        builder.append(event.getCorrelationId());
        builder.append("-");
        builder.append(event.getDescription());
        builder.append(" -> START_PAYLOAD");
        int counter = 1;
        for (Object obj :
                event.getPayload().getTarget()) {
            builder.append("-[" + counter++ + ":");
            builder.append(serializeObject(obj));
            builder.append("]");

        }
        builder.append("END_PAYLOAD");
        return trimString(builder.toString());
    }

    protected String trimString(String str) {

        String line_sep = System.getProperty("line.separator");
        str = str.trim().replaceAll(line_sep, "");
        str = str.trim().replaceAll("\n", "");

        return str;
    }
}
