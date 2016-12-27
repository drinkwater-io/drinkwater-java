package drinkwater.core.rest;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by A406775 on 20/12/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({PARAMETER})
public @interface Param {
    public String value();
}
