package drinkwater.core.internal;

/**
 * Created by A406775 on 2/01/2017.
 */
public class CoreCamelContext {


//    public static final String DW_STATICHANDLER = "dw-statichandler";
//
//    public static ResourceHandler getResourceHandler() {
//        ResourceHandler staticHandler = new ResourceHandler();
//        staticHandler.setBaseResource(Resource.newClassPathResource("/www"));
//        return staticHandler;
//    }


//    public void registerCoreBeans(){
//        registerBean(DW_STATICHANDLER, getResourceHandler());
//    }
//
//    public DefaultCamelContext getCamelContext() {
//        return camelContext;
//    }

//    public void start() {
//
//        try {
//            camelContext.addRoutes(createCoreRoutes());
//            camelContext.start();
//        } catch (Exception e) {
//            throw new RuntimeException("unable to start the core context", e);
//        }
//    }

//    public void stop(){
//        try {
//            camelContext.stop();
//        } catch (Exception e) {
//            throw new RuntimeException("unable to stop core context");
//        }
//
//    }
//
//    public void registerBean(String beanName, Object bean){
//        registerBean(camelContext.getRegistry(),  beanName,  bean);
//    }

//    private void registerBean(Registry registry, String beanName, Object bean){
//        if(registry instanceof SimpleRegistry){
//            ((SimpleRegistry) registry).put(beanName, bean);
//        }
//        else if(registry instanceof PropertyPlaceholderDelegateRegistry){
//            Registry wrappedRegistry = ((PropertyPlaceholderDelegateRegistry) registry).getRegistry();
//            registerBean(wrappedRegistry, beanName, bean);
//        }
//        else if(registry instanceof JndiRegistry){
//            ((JndiRegistry) registry).bind(beanName, bean);
//        }
//        else {
//            throw new RuntimeException("could not identify the registry type while registering core beans");
//        }
//
//    }

}
