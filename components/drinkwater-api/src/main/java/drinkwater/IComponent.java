package drinkwater;

public interface IComponent<B extends IBuilder> {
    B getBuilder();
}
