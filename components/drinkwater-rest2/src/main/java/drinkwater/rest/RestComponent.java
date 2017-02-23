package drinkwater.rest;

import drinkwater.IComponent;

public class RestComponent implements IComponent<IRestServiceBuilder> {

    int inRestCom;


    @Override
    public IRestServiceBuilder getBuilder() {
        return new IRestServiceBuilder();
    }

    public int getInRestCom() {
        return inRestCom;
    }

    public void setInRestCom(int inRestCom) {
        this.inRestCom = inRestCom;
    }
}
