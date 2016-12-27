package drinkwater.core;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Created by A406775 on 20/12/2016.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD})
public @interface DrinkWaterDataSource {
}
