package drinkwater.trace;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

/**
 * Created by A406775 on 6/01/2017.
 */
public class FileEventLogger extends AbstractEventLogger {

    public String folder;

    public String prefixFileName;

    public Path currentFile;

    public long maxSize = 1000000;

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
            String serializedEvent = serializeEvent(event) + System.lineSeparator();
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