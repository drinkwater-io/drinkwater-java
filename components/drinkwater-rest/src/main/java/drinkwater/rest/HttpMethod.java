package drinkwater.rest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Created by A406775 on 2/01/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface HttpMethod {

    String value() default "GET";

}
