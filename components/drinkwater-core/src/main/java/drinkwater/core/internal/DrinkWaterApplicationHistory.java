package drinkwater.core.internal;

import drinkwater.ServiceConfiguration;
import drinkwater.core.DrinkWaterApplication;
import drinkwater.helper.json.CustomJacksonObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by A406775 on 4/01/2017.
 */
public class DrinkWaterApplicationHistory {

    public String info;

    public LocalDateTime timestamp;

    public String applicationState;

    public DrinkWaterApplicationHistory(LocalDateTime timestamp, String applicationState, String info) {
        this.info = info;
        this.timestamp = timestamp;
        this.applicationState = applicationState;
    }

    public static DrinkWaterApplicationHistory createApplicationHistory(DrinkWaterApplication app) {
        try {
            CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
            String config = mapper.writeValueAsString(app.configuration().getConfigurations());

            return new DrinkWaterApplicationHistory(LocalDateTime.now(), config, "noInfoNow");
        } catch (Exception e) {
            throw new RuntimeException("exception while serializing : be carrefull with MOCK objects, it can lead to infinite loops", e);
        }
    }

    public static java.util.List<ServiceConfiguration> getConfig(DrinkWaterApplicationHistory dwah) {
        try {
            CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
            java.util.List<ServiceConfiguration> config = mapper.readValue(dwah.applicationState, mapper.getTypeFactory().constructCollectionType(List.class, ServiceConfiguration.class));

            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
