package drinkwater.trace;

/**
 * Created by A406775 on 5/01/2017.
 */
public class Payload {
    public Object[] targets;

    public Payload(Object... targets) {
        this.targets = targets;
    }

    public static Payload of(Object... obj) {
        return new Payload(obj);
    }

    public Object[] getTarget() {
        return targets;
    }
}
