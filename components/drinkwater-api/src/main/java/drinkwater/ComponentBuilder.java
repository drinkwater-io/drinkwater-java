package drinkwater;

public class ComponentBuilder {

    public  <B extends Builder> B as(Class<? extends IBuilderProvider<B>> clazz){
        try {
            IBuilderProvider c = clazz.newInstance();
            B answer = (B)c.getBuilder();
            return answer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
