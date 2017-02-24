package drinkwater;

public class Builder {
    String name;

    public Builder named(String name){
        this.name = name;
        return this;
    }
}
