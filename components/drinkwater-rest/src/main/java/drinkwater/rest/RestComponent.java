package drinkwater.rest;

import com.mashape.unirest.http.Unirest;
import drinkwater.IBuilderProvider;

import java.io.IOException;

public class RestComponent implements IBuilderProvider<RestServiceBuilder> {

    RestServiceBuilder _curBuilder = new RestServiceBuilder();
    @Override
    public RestServiceBuilder getBuilder() {
        return _curBuilder;
    }


}
