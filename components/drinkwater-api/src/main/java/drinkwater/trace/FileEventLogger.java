package drinkwater.trace;

import com.fasterxml.jackson.core.JsonProcessingException;
import drinkwater.IBaseEventLogger;
import drinkwater.helper.json.CustomJacksonObjectMapper;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Map;

/**
 * Created by A406775 on 6/01/2017.
 */
public class FileEventLogger implements IBaseEventLogger {

    public String folder;

    public String prefixFileName;

    public Path currentFile;

    public long maxSize = 1000000;

    private static String trimString(String str) {

        String line_sep = System.getProperty("line.separator");
        str = str.trim().replaceAll(line_sep, "");
        str = str.trim().replaceAll("\n", "");

        return str;
    }

    private static String serializeEvent(BaseEvent event) throws JsonProcessingException {
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
            serializeObject(builder, obj);
            builder.append("]");

        }
        builder.append("END_PAYLOAD");
        return builder.toString();
    }

    private static void serializeObject(StringBuilder builder, Object obj) throws JsonProcessingException {

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
    }

    @Override
    public void logEvent(BaseEvent event) {
        try {
            Method m = (Method) event.getPayload().getTarget()[0];
            if (m.getName().equals("toString")) {
                return;
            }
        } catch (Exception e) {
        }

        if (folder == null) {
            throw new RuntimeException("no folder specified : you must specify a folder first");
        }

        if (prefixFileName == null) {
            prefixFileName = "application-tracing";
        }

        writeToFile(event);
    }

    public synchronized void writeToFile(BaseEvent event) {
        try {
            String serializedEvent = trimString(serializeEvent(event)) + System.getProperty("line.separator");
            Files.write(getFilePath(), serializedEvent.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.SYNC);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized Path getFilePath() throws Exception {
        if (currentFile == null) {
            String fileName = prefixFileName + "-" + Instant.now().toEpochMilli() + ".log";
            currentFile = Paths.get(folder, fileName);
            Files.createFile(currentFile);
        }
        if (Files.size(currentFile) >= maxSize) {
            currentFile = null;
            currentFile = getFilePath();
        }
        return currentFile;
    }

}