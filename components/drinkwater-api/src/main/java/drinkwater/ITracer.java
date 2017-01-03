package drinkwater;

/**
 * Created by A406775 on 2/01/2017.
 */
public interface ITracer {

    void start(Object exchange);

    void stop(Object exchange);
}
