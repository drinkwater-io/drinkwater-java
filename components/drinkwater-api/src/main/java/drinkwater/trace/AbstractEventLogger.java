package drinkwater.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import drinkwater.IBaseEventLogger;
import drinkwater.helper.json.CustomJacksonObjectMapper;

import java.lang.reflect.Method;
import java.util.Map;

public abstract class AbstractEventLogger implements IBaseEventLogger {

    protected String serializeObject(Object obj) throws JsonProcessingException {

        if (obj == null) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        if (obj instanceof Operation) {
            builder.append(obj.toString());
        } else if (obj instanceof Method) {
            builder.append(((Method) obj).getName());
        } else if (obj instanceof String) {
            builder.append(obj);
        } else if (obj instanceof Map) {
            ((Map) obj).keySet().forEach(
                    key -> builder.append(" (" + key + " --> " + ((Map) obj).get(key) + ")"));

        } else if (obj instanceof Exception) {
            Exception exc = (Exception) obj;
            builder.append(serializeException(exc));

        } else {
            CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper(false);
            builder.append(mapper.writeValueAsString(obj));
        }

        return builder.toString();
    }

    private String serializeException(Exception ex) {
        StringBuilder builder = new StringBuilder();
        String lineseparator = System.getProperty("line.separator");

        builder.append("Exception message  :" + ex.getMessage() + lineseparator);
        builder.append("Exception stack  :" + lineseparator);

        for (StackTraceElement stackElement :
                ex.getStackTrace()) {
            builder.append(stackElement.toString() + lineseparator);
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
        str = str.trim().replaceAll(System.lineSeparator(), "");
        str = str.trim().replaceAll("\n", ""); //for hardcode \n

        return str;
    }
}
