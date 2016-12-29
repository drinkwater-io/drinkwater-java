package drinkwater.cdi;

import drinkwater.core.DrinkWaterApplication;
import drinkwater.core.ServiceConfigurationBuilder;
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

    void addDrinkWater(@Observes AfterBeanDiscovery abdEvent, BeanManager manager){
        abdEvent.addBean(new DrinkWaterApplicationBean());
    }

    void configureDrinkWater(@Observes AfterDeploymentValidation adv, BeanManager manager) throws Exception {


        DrinkWaterApplication dwapp =
                (DrinkWaterApplication) manager.getReference(
                        manager.resolve(manager.getBeans(DrinkWaterApplication.class)),
                        DrinkWaterApplication.class,
                        manager.createCreationalContext(null)
                );

        for (Bean<?> serviceConfigBean :manager.getBeans(ServiceConfigurationBuilder.class)) {

            ServiceConfigurationBuilder builder = (ServiceConfigurationBuilder)manager.getReference(serviceConfigBean,
                    ServiceConfigurationBuilder.class,
                    manager.createCreationalContext(serviceConfigBean));

            dwapp.addServiceBuilder(builder);

        }

        dwapp.start();

    }

    void stopDrinkWaterApplication(@Observes BeforeShutdown bsd, BeanManager mgr) throws Exception {
        DrinkWaterApplication dwapp =
                (DrinkWaterApplication) mgr.getReference(
                        mgr.resolve(mgr.getBeans(DrinkWaterApplication.class)),
                        DrinkWaterApplication.class,
                        mgr.createCreationalContext(null)
                );

        dwapp.stop();

    }

}
