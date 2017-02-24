package drinkwater.rest;

import drinkwater.IBuilderProvider;

public class RestComponent implements IBuilderProvider<IRestServiceBuilder> {

    @Override
    public IRestServiceBuilder getBuilder() {
        return new IRestServiceBuilder();
    }

}
