package drinkwater.unit.test.model.forRest;


        import java.io.InputStream;
        import java.util.Map;

public interface IServiceA {
    FileReadResult upload(InputStream stream, String fromPath);

    String getInfo(String someValue);

    String getMethodWithMap(Map<String,Object> paramAsMap, String another_param);
}
