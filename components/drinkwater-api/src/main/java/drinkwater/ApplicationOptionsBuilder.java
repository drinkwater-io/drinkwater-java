package drinkwater;

public interface ApplicationOptionsBuilder {
    static ApplicationOptions options(){
        return new ApplicationOptions(false, false);
    }

    static ApplicationOptions tracedApplication(){
        return options().useTracing();
    }
}
