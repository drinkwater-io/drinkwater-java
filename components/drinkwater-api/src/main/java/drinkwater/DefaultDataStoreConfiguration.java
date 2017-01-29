package drinkwater;

public class DefaultDataStoreConfiguration implements IDataStoreConfiguration {

    private String name;

    private Class implementingClass;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class getImplementingClass() {
        return implementingClass;
    }

    public void setImplementingClass(Class implementingClass) {
        this.implementingClass = implementingClass;
    }
}
