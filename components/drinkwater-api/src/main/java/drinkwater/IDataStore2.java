package drinkwater;

public interface IDataStore2 extends IDataStore{

    void configure() throws Exception;

//    IPropertyResolver getPropertyResolver();
//
//    String getName();
//
//    default String getProperty(String property) throws Exception{
//        String key = String.format("datastore.%s.%s", getName(), property);
//        return getPropertyResolver().lookupProperty(key);
//    }
}
