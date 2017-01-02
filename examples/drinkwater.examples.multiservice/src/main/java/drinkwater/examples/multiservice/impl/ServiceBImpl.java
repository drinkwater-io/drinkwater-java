package drinkwater.examples.multiservice.impl;

import drinkwater.examples.multiservice.IServiceB;
import drinkwater.examples.multiservice.IServiceC;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceBImpl implements IServiceB {

    private IServiceC serviceC;

    @Override
    public String getTransformedData(String dataInfo) {
        String fromC = serviceC.findData(dataInfo);

        return String.format("Transformed in B : %s", fromC);
    }
}
