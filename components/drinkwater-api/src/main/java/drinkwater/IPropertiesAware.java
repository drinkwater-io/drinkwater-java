package drinkwater;

import java.util.Properties;

public interface IPropertiesAware {
    Properties getInitialProperties();

    String[] getPropertiesLocations();

    String getpropertiesPrefix();

    String getPropertiesDefaultName();
}
