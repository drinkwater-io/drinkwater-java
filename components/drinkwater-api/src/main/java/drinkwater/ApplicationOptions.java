package drinkwater;

public class ApplicationOptions {

    private boolean autoStart;

    private boolean useTracing;

    private boolean useServiceManagement;

    private Class applicationBuilderClass;

    private Class eventLoggerClass;


    public ApplicationOptions(boolean useTracing, boolean useServiceManagement) {
        this.useTracing = useTracing;
        this.useServiceManagement = useServiceManagement;
    }

    public boolean isUseTracing() {
        return useTracing;
    }

    public void setUseTracing(boolean useTracing) {
        this.useTracing = useTracing;
    }

    public Class getApplicationBuilderClass() {
        return applicationBuilderClass;
    }

    public Class getEventLoggerClass() {
        return eventLoggerClass;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public boolean isUseServiceManagement() {
        return useServiceManagement;
    }


    public ApplicationOptions and(){
        return this;
    }


    public static ApplicationOptions defaultInstance() {
        return new ApplicationOptions(false, false);
    }

    public ApplicationOptions withServicemanagement() {
        this.useServiceManagement = true;
        return this;
    }

    public ApplicationOptions useTracing() {
        this.useTracing = true;
        return this;
    }

    public ApplicationOptions use(Class clazz){
        if(IApplicationBuilder.class.isAssignableFrom(clazz)){
            return useBuilder(clazz);
        }
        else if(IBaseEventLogger.class.isAssignableFrom(clazz)){
            return useEventLogger(clazz);
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

    public ApplicationOptions useEventLogger(Class clazz) {
        this.eventLoggerClass = clazz;
        return this;
    }

    public ApplicationOptions autoStart() {
        this.autoStart = true;
        return this;
    }


    public static ApplicationOptions from(IPropertyResolver resolver) {
        try {
            boolean useTracing =
                    (Boolean) resolver.lookupProperty(Boolean.class, "useTracing:false");

            boolean useServiceManagement =
                    (Boolean) resolver.lookupProperty(Boolean.class, "useServiceManagement:false");

            return new ApplicationOptions(useTracing, useServiceManagement);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
