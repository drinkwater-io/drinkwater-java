package test.drinkwater.core.model.forStore;

import drinkwater.IDataStore;

import java.io.IOException;

public class SimpleTestStore implements IDataStore {

    private String user;

    private int propertyInt;

    @Override
    public void migrate() {
        System.out.println(user);
    }

    @Override
    public void start() throws Exception {
        System.out.println("");
    }

    @Override
    public void close() throws IOException {
        System.out.println("");
    }


    @Override
    public void configure() throws Exception {

    }
}
