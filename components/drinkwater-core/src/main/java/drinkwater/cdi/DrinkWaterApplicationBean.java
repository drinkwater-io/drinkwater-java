package drinkwater.cdi;

import drinkwater.core.DrinkWaterApplication;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * Created by A406775 on 29/12/2016.
 */
public class DrinkWaterApplicationBean  implements Bean<DrinkWaterApplication>{
    @Override
    public Class<?> getBeanClass() {
        return DrinkWaterApplication.class;
    }

    @Override
    public DrinkWaterApplication create(CreationalContext<DrinkWaterApplication> creationalContext) {
        return new DrinkWaterApplication();
    }

    @Override
    public void destroy(DrinkWaterApplication instance, CreationalContext<DrinkWaterApplication> creationalContext) {

    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<Type> getTypes() {
        return Collections.singleton((Type) DrinkWaterApplication.class);
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.singleton((Annotation) new AnnotationLiteral<Default>(){});
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public String getName() {
        return "drink-water-app-cdi";
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }
}
