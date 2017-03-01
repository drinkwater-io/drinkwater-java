package test.drinkwater.rest.model;
import drinkwater.rest.HttpMethod;
import drinkwater.rest.Path;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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

    @Override
    public String getMethodWithMap(Map<String, Object> paramAsMap, String another_param) {
        final StringBuilder result = new StringBuilder();

        result.append("paramAsMap=[");
        paramAsMap.keySet().forEach(s -> {
            result.append("(" + s);
            result.append(":");
            result.append(paramAsMap.get(s)+ ")");
        });
        result.append("] - ");
        result.append("another_param="+another_param);

        return result.toString();
    }
}
