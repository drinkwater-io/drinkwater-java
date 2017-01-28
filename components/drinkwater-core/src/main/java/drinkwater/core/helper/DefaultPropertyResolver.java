//package drinkwater.core.helper;
//
//import drinkwater.IPropertyResolver;
//
///**
// * Created by A406775 on 30/12/2016.
// */
//public class DefaultPropertyResolver implements IPropertyResolver {
//
//    private Service config;
//
//    public DefaultPropertyResolver(Service config) {
//        this.config = config;
//    }
//
//    @Override
//    public String lookupProperty(String uri) throws Exception {
//        return config.lookupProperty(uri);
//    }
//
//    @Override
//    public Object lookupProperty(Class resultType, String uri)throws Exception{
//        String value = lookupProperty(uri);
//        return this.config.getCamelContext().getTypeConverter().convertTo(resultType, value);
//    }
//
//}
