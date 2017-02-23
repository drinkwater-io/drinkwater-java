package drinkwater.rest;

import drinkwater.IBuilder;

public class IRestServiceBuilder implements IBuilder {

    public IRestServiceBuilder withPort(int port){
        return this;
    }
}
