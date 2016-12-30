package examples.drinkwater.drinktracker.asrest;

import baseline.BaseLinefactory;
import drinkwater.IServiceConfiguration;
import drinkwater.InjectionStrategy;
import drinkwater.ServiceConfiguration;
import drinkwater.ServiceConfigurationBuilder;
import examples.drinkwater.drinktracker.model.IWaterVolumeRepository;

import java.util.List;

public class ServiceConfigurationAsRest extends ServiceConfigurationBuilder {
    @Override
    public List<IServiceConfiguration> build() {

        List<ServiceConfiguration> baseLine = BaseLinefactory.createServices();

        ServiceConfiguration waterRepo = javaslang.collection.List.ofAll(baseLine)
                .filter(s -> s.getServiceClass().equals(IWaterVolumeRepository.class))
                .get();

        waterRepo.withProperties("classpath:drinktracker.properties")
                .withInjectionStrategy(InjectionStrategy.Default);

        return javaslang.collection.List.ofAll(baseLine)
                .map(s -> s.useBeanClass(s.getTargetBean().getClass()).asRest()) //use class instead of Bean Object
                .map(s -> (IServiceConfiguration) s) // cast
                .toJavaList();
    }
}
