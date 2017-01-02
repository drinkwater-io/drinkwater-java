package drinkwater.examples.multiservice.impl;

import drinkwater.examples.multiservice.IServiceC;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceCImpl implements IServiceC {

    @Override
    public String findData(String searchInfo) {
        return String.format("from C : %s", searchInfo);
    }
}
