package drinkwater.rest;

import drinkwater.IBuilderProvider;

public class RestComponent implements IBuilderProvider<RestServiceBuilder> {

    RestServiceBuilder _curBuilder = new RestServiceBuilder();
    @Override
    public RestServiceBuilder getBuilder() {
        return _curBuilder;
    }
}
