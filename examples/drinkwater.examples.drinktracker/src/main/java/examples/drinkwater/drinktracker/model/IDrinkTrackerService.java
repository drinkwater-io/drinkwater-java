package examples.drinkwater.drinktracker.model;

import java.util.List;

/**
 * Created by A406775 on 28/12/2016.
 */
public interface IDrinkTrackerService {

    String saveVolume(Account account, int volume) throws Exception;

    List<String> getVolumes(Account account) throws Exception;

    void clearVolumes(Account account) throws Exception;
}
