package drinkwater.examples.numberservice;

/**
 * Created by A406775 on 27/12/2016.
 */
public class NumberFormatter implements INumberFormatter {

    @Override
    public String prependZero(String s){

        //cpu time consumption
        consumeCpuFor(Constants.LATENCY);

        if(s == null){
            return "0";
        }

        return "0" + s;
    }

    private void consumeCpuFor(int milliseconds) {
        long sleepTime = milliseconds*1000000L; // convert to nanoseconds
        long startTime = System.nanoTime();
        while ((System.nanoTime() - startTime) < sleepTime) {
        }
    }
}
