package drinkwater.core;

/**
 * Created by A406775 on 23/12/2016.
 */
public class ServiceConfiguration {
    private String propertiesFileLocation;

    private String contextPath;

    private Class serviceClass;

    public ServiceConfiguration(){}

    public ServiceConfiguration(Class serviceClass, String contextPath, String propertiesFileLocation) {
        this.propertiesFileLocation = propertiesFileLocation;
        this.contextPath = contextPath;
        this.serviceClass = serviceClass;
    }

    public String getPropertiesFileLocation() {
        return propertiesFileLocation;
    }

    public void setPropertiesFileLocation(String propertiesFileLocation) {
        this.propertiesFileLocation = propertiesFileLocation;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String toString() {
        return "ServiceConfiguration{" +
                "propertiesFileLocation='" + propertiesFileLocation + '\'' +
                '}';
    }

    public String getProperty(String key){
        return this.getContextPath() + key;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }
}
