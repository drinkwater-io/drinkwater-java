package drinkwater;

public class ApplicationOptions {

    private boolean autoStart;

    private Class applicationBuilderClass;

    public ApplicationOptions() {

    }

    public Class getApplicationBuilderClass() {
        return applicationBuilderClass;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public ApplicationOptions and(){
        return this;
    }

    public ApplicationOptions use(Class clazz){
        if(IApplicationBuilder.class.isAssignableFrom(clazz)){
            return useBuilder(clazz);
        }
        else{
            throw new RuntimeException("Application configuration exception, could not assign " + clazz +  " because it's not an inheritance of"
            + ApplicationBuilder.class.getName() + " or " + IBaseEventLogger.class);
        }
    }

    public ApplicationOptions useBuilder(Class clazz) {
        this.applicationBuilderClass = clazz;
        return this;
    }

    public ApplicationOptions autoStart() {
        this.autoStart = true;
        return this;
    }


//    public static ApplicationOptions from(IPropertyResolver resolver) {
//        try {
//            boolean useTracing =
//                    (Boolean) resolver.lookupProperty(Boolean.class, "useTracing:false");
//
//            boolean useServiceManagement =
//                    (Boolean) resolver.lookupProperty(Boolean.class, "useServiceManagement:false");
//
//            return new ApplicationOptions(useTracing, useServiceManagement);
//
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//    }


}
