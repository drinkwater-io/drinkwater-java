package drinkwater.core.rest;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Created by A406775 on 20/12/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, PARAMETER})
public @interface Path {
    public String value();
}
