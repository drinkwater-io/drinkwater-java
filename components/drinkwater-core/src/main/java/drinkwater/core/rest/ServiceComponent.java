package drinkwater.core.rest;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.io.FileReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD})
public @interface ServiceComponent {

    public boolean exposeAsRest() default true;

    @Nonbinding public String name() default "";

    @Nonbinding public String restPath() default "";

    @Nonbinding public String restGetPrefix() default "get,find,all";

    @Nonbinding public String restPostPrefix() default "create,new,post";
}