package drinkwater.examples.getstarted2;

public class SimpleServiceImpl implements ISimpleService {

    public String prefix;

    @Override
    public String ping(String message) {
        return String.format("%s %s", prefix, message);
    }
}
