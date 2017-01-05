package drinkwater.examples.multiservice.impl;

import drinkwater.examples.multiservice.IServiceD;

/**
 * Created by A406775 on 3/01/2017.
 */
public class ServiceDImpl implements IServiceD {

    private String textToAppend;

    @Override
    public String uppercaseData(String data) {

        return "D -> [" + data.toUpperCase() + textToAppend + "]";
    }
}
