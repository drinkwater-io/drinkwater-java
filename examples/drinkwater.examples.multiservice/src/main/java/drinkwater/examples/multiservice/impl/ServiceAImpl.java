package drinkwater.examples.multiservice.impl;

import drinkwater.examples.multiservice.IServiceA;
import drinkwater.examples.multiservice.IServiceB;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceAImpl implements IServiceA {

    IServiceB serviceB;

    @Override
    public String getData(String data) {
        String fromServiceB = serviceB.getTransformedData(data);

        return String.format("service A returns : %s", fromServiceB);
    }
}
