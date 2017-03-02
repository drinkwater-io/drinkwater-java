package drinkwater.test;

public class SimpleTestServiceImpl implements ISimpleTestService {
    @Override
    public String echo(String message) {
        return message;
    }

    @Override
    public String sayHello(String name) {
        return "hello " + name;
    }

    @Override
    public int add(int one, int two) {
        return one + two;
    }
}
