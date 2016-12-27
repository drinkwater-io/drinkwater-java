package drinkwater.core;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by A406775 on 23/12/2016.
 */

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD})
public @interface DrinkWaterApplicationConfig {

    @Nonbinding public String configurationMethod() default "getServiceConfigurations";
}
