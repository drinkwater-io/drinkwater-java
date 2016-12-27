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
public class NumberFileRepository implements INumberRepository {


    public String directory;

    public NumberFileRepository() {
    }

    public NumberFileRepository(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public synchronized void saveNumber(Account account, String number) {
        try {
            number = number + "\n";
            Thread.sleep(200);
            Files.write(Paths.get(directory, account.getAcountId() + ".txt"), number.getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getNumbers(Account account) {

        List<String> lines = null;

        try {
            lines =
                    Files.readAllLines(Paths.get(directory, account.getAcountId() + ".txt"), Charset.defaultCharset());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;

    }

    public synchronized void clearNumbers(Account account) {
        try {

            Files.delete(Paths.get(directory, account.getAcountId() + ".txt"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
