package drinkwater;

import org.apache.camel.CamelContext;

public class ComponentBuilder<B extends Builder> {

    private B builder;

    private Class serviceClass;
    private Object object;

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    public B getBuilder(){
        return builder;
    }

    public  B as(Class<? extends IBuilderProvider<B>> clazz){
        try {
            IBuilderProvider c = clazz.newInstance();
            builder = (B)c.getBuilder();
            if(this.getObject() != null) {
                builder.setObject(this.getObject());
            }
            else {
                builder.setServiceClass(getServiceClass());
            }
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    //    public  <B extends Builder> B as(Class<? extends IBuilderProvider<B>> clazz){
//        try {
//            IBuilderProvider c = clazz.newInstance();
//            B answer = (B)c.getBuilder();
//            return answer;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
