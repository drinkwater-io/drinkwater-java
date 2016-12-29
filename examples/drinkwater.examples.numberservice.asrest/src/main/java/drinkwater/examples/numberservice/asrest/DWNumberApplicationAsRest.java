package drinkwater.examples.numberservice.asrest;

import drinkwater.core.DrinkWaterApplicationConfig;
import drinkwater.core.InjectionStrategy;
import drinkwater.core.ServiceConfiguration;
import drinkwater.core.ServiceConfigurationCollection;
import drinkwater.examples.numberservice.*;

@DrinkWaterApplicationConfig
public class DWNumberApplicationAsRest {
    public ServiceConfigurationCollection getServiceConfigurations(){

        ServiceConfiguration numberRepositoryService =ServiceConfiguration
                .forService(INumberRepository.class)
                .withProperties("classpath:numberRepositoryService.properties")
                .useBean(NumberFileRepository.class)
                .withInjectionStrategy(InjectionStrategy.Default);

        ServiceConfiguration accountService =ServiceConfiguration
                .forService(IAccountService.class)
                .useBean(AccountService.class);

        ServiceConfiguration numberFormatter =ServiceConfiguration
                .forService(INumberFormatter.class)
                .useBean(NumberFormatter.class);

        ServiceConfiguration numberServiceAsRest =ServiceConfiguration
                .forService(INumberService.class)
                .useBean(NumberService.class)
                .asRest()
                .dependsOn(accountService, numberFormatter, numberRepositoryService);

        //FIXME order is important here, it should not be
        return ServiceConfigurationCollection.of(
                accountService,
                numberFormatter,
                numberRepositoryService,
                numberServiceAsRest);
    }
}
