package drinkwater;

public class ComponentBuilder {

    public <C extends IComponent> C as(Class<C> clazz){
        try {
            IComponent c = clazz.newInstance();
            return (C)c;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  <B extends IBuilder> B asBuilder(Class<? extends IComponent<B>> clazz){
        try {
            IComponent c = clazz.newInstance();
            B answer = (B)c.getBuilder();
            return answer;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
