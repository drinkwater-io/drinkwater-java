package test.drinkwater.core.model.forRest;

import drinkwater.rest.HttpMethod;
import drinkwater.rest.Path;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ServiceAImpl implements IServiceA {

    @HttpMethod("POST")
    @Path("/upload")
    @Override
    public FileReadResult upload(InputStream file, String fromPath) {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8))) {
            String fileContent = buffer.lines().collect(Collectors.joining("\n"));
            return new FileReadResult(fileContent + " uploaded");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Path("/info/{someValue}")
    @Override
    public String getInfo(String someValue) {

        return "pong " + someValue;
    }
}
