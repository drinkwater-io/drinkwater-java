package drinkwater;

/**
 * Created by A406775 on 30/12/2016.
 */
public interface IPropertyResolver {

    String lookupProperty(String uri) throws Exception;

    Object lookupProperty(Class resultType, String uri) throws Exception;

}
