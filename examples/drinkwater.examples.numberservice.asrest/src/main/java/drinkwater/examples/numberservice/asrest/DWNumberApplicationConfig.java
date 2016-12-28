package drinkwater.examples.numberservice.asrest;

import drinkwater.core.DrinkWaterApplicationConfig;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import drinkwater.core.ServiceConfigurationCollection;
import drinkwater.examples.numberservice.*;

@DrinkWaterApplicationConfig
public class DWNumberApplicationConfig {
    public ServiceConfigurationCollection getServiceConfigurations(){

        ServiceConfiguration numberRepositoryService =ServiceConfiguration
                .forService(INumberRepository.class)
                .withProperties("classpath:first-number-repository.properties")
                .useBean(NumberFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        ServiceConfiguration accountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBean(AccountService.class);

        ServiceConfiguration numberFormatter =ServiceConfiguration
                .forService(INumberFormatter.class)
                .useBean(NumberFormatter.class);

        ServiceConfiguration numberService =ServiceConfiguration
                .forService(INumberService.class)
                .useBean(NumberService.class)
                .dependsOn(accountService, numberFormatter, numberRepositoryService);

        //FIXME order is important here, we should sort by deps...
        return ServiceConfigurationCollection.of(
                accountService,
                numberFormatter,
                numberRepositoryService,
                numberService);
    }
}
