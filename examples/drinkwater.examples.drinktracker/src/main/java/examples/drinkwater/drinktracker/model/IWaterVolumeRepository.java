package examples.drinkwater.drinktracker.model;

import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public interface IWaterVolumeRepository {

    void saveWaterVolume(Account account, String volume);

    List<String> getVolumes(Account account);

    void clearVolumes(Account account);
}
