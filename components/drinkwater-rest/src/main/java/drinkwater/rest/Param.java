package drinkwater.rest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Created by A406775 on 20/12/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PARAMETER})
public @interface Param {
    public String value();
}
