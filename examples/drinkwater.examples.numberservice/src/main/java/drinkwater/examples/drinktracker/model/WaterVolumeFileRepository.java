package drinkwater.examples.drinktracker.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public class WaterVolumeFileRepository implements IWaterVolumeRepository {

    public String directory;

    public WaterVolumeFileRepository() {
    }

    public WaterVolumeFileRepository(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public synchronized void saveWaterVolume(Account account, String volume) {
        try {
            volume = volume + "\n";
            Thread.sleep(Constants.LATENCY);
            Files.write(Paths.get(directory, "water-volumes-of-" + account.getAcountId() + ".txt"), volume.getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getVolumes(Account account) {

        List<String> lines = null;

        try {
            lines =
                    Files.readAllLines(Paths.get(directory, account.getAcountId() + ".txt"), Charset.defaultCharset());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;

    }

    public synchronized void clearVolumes(Account account) {
        try {

            Files.delete(Paths.get(directory, account.getAcountId() + ".txt"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
