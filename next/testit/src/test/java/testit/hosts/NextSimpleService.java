package testit.hosts;

import javax.inject.Singleton;

/**
 * Created by A406775 on 24/03/2017.
 */
@Singleton
public class NextSimpleService {

    private double random = Math.random();
    public String ping(String what){
        return "pong : " + what + " " + random;
    }
}
