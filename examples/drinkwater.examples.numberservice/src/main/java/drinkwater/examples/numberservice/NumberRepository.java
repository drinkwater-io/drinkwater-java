package drinkwater.examples.numberservice;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public class NumberRepository implements INumberRepository {

    public static List<String> numbers = new ArrayList<>();


    @Override
    public synchronized void registerSomeInfo(String filePath, String info) {
        try {
            info = info + "\n";
            Thread.sleep(200);
            Files.write(Paths.get(filePath), info.getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getNumbers(String filePath) {

        List<String> lines = null;

        try {
            lines =
                    Files.readAllLines(Paths.get(filePath), Charset.defaultCharset());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;

    }

    public synchronized void clear(String filePath) {
        try {

            Files.delete(Paths.get(filePath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
