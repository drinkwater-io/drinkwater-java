package drinkwater.core;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by A406775 on 23/12/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target({TYPE, FIELD})
public @interface ServiceQualifier {
    public String value();
}
