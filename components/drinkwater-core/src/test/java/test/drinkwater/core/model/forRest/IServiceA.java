package test.drinkwater.core.model.forRest;

import java.io.InputStream;

public interface IServiceA {
    FileReadResult upload(InputStream stream, String fromPath);

    String getInfo(String someValue);
}
