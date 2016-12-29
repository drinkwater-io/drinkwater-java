package drinkwater.core.helper;

import drinkwater.core.DrinkWaterApplication;
import javaslang.collection.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.Method;
import java.util.Set;

import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withReturnType;

/**
 * Created by A406775 on 27/12/2016.
 */
public class DrinkWaterExtension implements Extension {

    void addAppConfigToTheMix(@Observes BeforeBeanDiscovery bbd, BeanManager mgr){
        //bbd.addAnnotatedType( mgr.createAnnotatedType(DrinkWaterApplicationConfig.class));
    }

    void startDrinkWaterApplication(@Observes AfterDeploymentValidation adv, BeanManager mgr) throws Exception {
        DrinkWaterApplication dwapp =
                (DrinkWaterApplication) mgr.getReference(
                        mgr.resolve(mgr.getBeans(DrinkWaterApplication.class)),
                        DrinkWaterApplication.class,
                        mgr.createCreationalContext(null)
                );
//
        dwapp.start();

    }

//    void stopDrinkWaterApplication(@Observes BeforeShutdown bsd, BeanManager mgr) throws Exception {
//        DrinkWaterApplication dwapp =
//                (DrinkWaterApplication) mgr.getReference(
//                        mgr.resolve(mgr.getBeans(DrinkWaterApplication.class)),
//                        DrinkWaterApplication.class,
//                        mgr.createCreationalContext(null)
//                );
////
//        dwapp.stop();
//
//    }

}
