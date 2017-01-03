package drinkwater.examples.multiservice.impl;

import drinkwater.examples.multiservice.IServiceD;

/**
 * Created by A406775 on 3/01/2017.
 */
public class ServiceDImpl implements IServiceD {

    private String textToAppend;

    @Override
    public String uppercaseData(String data) {

        return String.format("C : [uppercase : %s - append : %s]", data.toUpperCase(), textToAppend);
    }
}
