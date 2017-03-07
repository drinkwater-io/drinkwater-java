package drinkwater.bean;

import drinkwater.IBuilderProvider;

public class BeanComponent implements IBuilderProvider<BeanServiceBuilder> {
    BeanServiceBuilder _builder = new BeanServiceBuilder();
    @Override
    public BeanServiceBuilder getBuilder() {
        return _builder;
    }
}
