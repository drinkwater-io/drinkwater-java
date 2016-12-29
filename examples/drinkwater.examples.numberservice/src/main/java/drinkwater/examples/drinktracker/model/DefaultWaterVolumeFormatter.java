package drinkwater.examples.drinktracker.model;

/**
 * Created by A406775 on 27/12/2016.
 */
public class DefaultWaterVolumeFormatter implements IWaterVolumeFormatter {

    @Override
    public String formatVolume(String volume){

        //cpu time consumption
        consumeCpuFor(Constants.LATENCY);

        if(volume == null){
            return "0";
        }

        return "0" + volume;
    }

    private void consumeCpuFor(int milliseconds) {
        long sleepTime = milliseconds*1000000L; // convert to nanoseconds
        long startTime = System.nanoTime();
        while ((System.nanoTime() - startTime) < sleepTime) {
        }
    }
}
