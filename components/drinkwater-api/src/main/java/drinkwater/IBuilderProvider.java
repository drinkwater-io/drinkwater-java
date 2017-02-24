package drinkwater;

public interface IBuilderProvider<B extends Builder> {
    B getBuilder();
}
