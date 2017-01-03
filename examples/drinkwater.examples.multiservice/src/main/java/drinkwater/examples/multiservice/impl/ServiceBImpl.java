package drinkwater.examples.multiservice.impl;

import drinkwater.examples.multiservice.IServiceB;
import drinkwater.examples.multiservice.IServiceC;
import drinkwater.examples.multiservice.IServiceD;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceBImpl implements IServiceB {

    private IServiceC serviceC;

    private IServiceD serviceD;

    @Override
    public String getTransformedData(String dataInfo) {
        String fromC = serviceC.findData(dataInfo);

        String fromD = serviceD.uppercaseData(fromC);

        return String.format("Using ServiceC with data : (%s) and received (%s) then calling Service D and received (%s)", dataInfo, fromC, fromD);
    }
}
