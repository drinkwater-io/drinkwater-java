package drinkwater;

import drinkwater.helper.GeneralUtils;
import drinkwater.helper.MapUtils;
import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.function.Function;

public class Configuration {
    private static Logger logger = LoggerFactory.getLogger(Configuration.class);

    CamelContext camelContext;
    String applicationName;
    PropertiesComponent propertiesComponent;

    public Configuration(String applicationName, CamelContext camelContext) {
        this.applicationName = applicationName;
        this.camelContext = camelContext;
        initProperties(camelContext);
    }

    public PropertiesComponent getPropertiesComponent() {
        if (propertiesComponent == null) {
            propertiesComponent = camelContext.getComponent(
                    "properties", PropertiesComponent.class);
        }
        return propertiesComponent;
    }

    public String addProperty(String key, String value){

        String val = (String)getPropertiesComponent()
                .getInitialProperties()
                .computeIfAbsent(key, (obj) -> value);

        return val;
    }


    public <T> T lookupProperty(Class clazz, String key){

        String value = null;
        try {
            value = getPropertiesComponent().parseUri(key);
        } catch (Exception e) {
            //TODO : manage exception here => add user warn ...
            e.printStackTrace();
            value = null;
        }

        return (T)camelContext.getTypeConverter().convertTo(clazz, value);
    }

    public String[] getPropertiesLocations() {

        ArrayList<String> properties = new ArrayList<>();
        //default file from classpath
        properties.add(0, "classpath:drinkwater-application.properties");
        properties.add(1, "classpath:" + applicationName + ".properties");

        try {
            //default file from file
            properties.add(2, "file:" +
                    Paths.get(GeneralUtils.getJarFolderPath(this.getClass()).toString(),
                            applicationName + ".properties"));
        } catch (Exception ex) {
            logger.warn("could not load properties for  application " + applicationName + " from file Location");
        }

        return properties.toArray(new String[0]);
    }

    private void initProperties(CamelContext context) {

        try {
            getPropertiesComponent().setIgnoreMissingLocation(true);

            Properties applicationProperties = loadProperties();

            getPropertiesComponent().setInitialProperties(applicationProperties);
        } catch (Exception ex) {
            throw new RuntimeException("could not initialize application properties", ex);
        }
    }

//    private static void initProperties(CamelContext context) {
//
//        try {
//            PropertiesComponent propertiesComponent = context.getComponent(
//                    "properties", PropertiesComponent.class);
//            propertiesComponent.setIgnoreMissingLocation(true);
//
//            String applicationPrefix = getpropertiesPrefix();
//            String servicePrefix = null;
//            if(servicePropertiesAware != null) {
//                servicePrefix = applicationPrefix + "." + servicePropertiesAware.getpropertiesPrefix();
//            }
//
//            Properties applicationProperties =
//                    loadProperties(applicationPrefix, applicationPropertiesAware);
//            Properties serviceProperties =
//                    loadProperties(servicePrefix, servicePropertiesAware);
//
//            Properties initialServiceProperties = new Properties();
//            if(servicePropertiesAware != null){
//                initialServiceProperties = MapUtils.prefixProperties(servicePrefix, servicePropertiesAware.getInitialProperties());
//            }
//
//            Properties initialApplicationProperties = new Properties();
//            if(servicePropertiesAware != null){
//                initialApplicationProperties = MapUtils.prefixProperties(applicationPrefix, applicationPropertiesAware.getInitialProperties());
//            }
//
//            Properties mergedProps = MapUtils.mergeProperties(
//                    applicationProperties,
//                    serviceProperties);
//            mergedProps = MapUtils.mergeProperties(
//                    initialApplicationProperties,
//                    mergedProps);
//            mergedProps =MapUtils.mergeProperties(
//                    initialServiceProperties,
//                    mergedProps);
//            propertiesComponent.setInitialProperties(mergedProps);
//        } catch (Exception ex) {
//            throw new RuntimeException("could not initialize application properties", ex);
//        }
//    }

    private Properties loadProperties() throws Exception {
        PropertiesResolver pr = new PropertiesResolver();
        return pr.resolveProperties(getPropertiesLocations());

    }
}
