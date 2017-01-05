package drinkwater.examples.scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A406775 on 5/01/2017.
 */
public class HelloHolderImpl implements IHelloHolder {
    private List<String> hellos = new ArrayList<>();

    @Override
    public void addHello(String hello) {
        hellos.add(hello);
    }

    public List<String> getHellos() {
        return hellos;
    }
}
