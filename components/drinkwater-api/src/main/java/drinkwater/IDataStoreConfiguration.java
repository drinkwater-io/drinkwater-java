package drinkwater;

public interface IDataStoreConfiguration {

    String getName();

    Class getImplementingClass();

    default Object getProperty(IPropertyResolver propertyResolver, Class propertyType, String property) throws Exception{
        String key = String.format("datastore.%s.%s", getName(), property);
        return propertyResolver.lookupProperty(propertyType, key);
    }

}
