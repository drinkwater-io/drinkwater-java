package examples.drinkwater.drinktracker.model;

import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public class DrinkTrackerService implements IDrinkTrackerService {

    private IWaterVolumeRepository waterVolumeRepository;

    private IWaterVolumeFormatter waterVolumeFormatter;

    private IAccountService accountService;

    public DrinkTrackerService() {
    }

    public DrinkTrackerService(
            IAccountService accountService,
            IWaterVolumeRepository waterVolumeRepository,
            IWaterVolumeFormatter waterVolumeFormatter) {
        this.accountService = accountService;
        this.waterVolumeRepository = waterVolumeRepository;
        this.waterVolumeFormatter = waterVolumeFormatter;
    }

    public IWaterVolumeRepository getWaterVolumeRepository() {
        return waterVolumeRepository;
    }

    public void setWaterVolumeRepository(IWaterVolumeRepository waterVolumeRepository) {
        this.waterVolumeRepository = waterVolumeRepository;
    }

    public IWaterVolumeFormatter getWaterVolumeFormatter() {
        return waterVolumeFormatter;
    }

    public void setWaterVolumeFormatter(IWaterVolumeFormatter waterVolumeFormatter) {
        this.waterVolumeFormatter = waterVolumeFormatter;
    }

    public IAccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    //API

    @Override
    public String saveVolume(Account account, int volume) throws Exception {

        checkAuthenticated(account);

        //convert volume to string
        String volumeAsString = Integer.toString(volume);

        //check that length route 10
        while (volumeAsString.length() < 5) {
            volumeAsString = waterVolumeFormatter.formatVolume(volumeAsString);
        }

        //register the info
        waterVolumeRepository.saveWaterVolume(account, volumeAsString);

        return volumeAsString;
    }

    @Override
    public List<String> getVolumes(Account account) throws Exception {
        checkAuthenticated(account);

        return waterVolumeRepository.getVolumes(account);
    }

    @Override
    public void clearVolumes(Account account) throws Exception {

        checkAuthenticated(account);

        waterVolumeRepository.clearVolumes(account);
    }

    private void checkAuthenticated(Account account) throws Exception {
        if (!accountService.isAuthenticated(account)) {
            throw new Exception("should authenticate first");
        }
    }

}
