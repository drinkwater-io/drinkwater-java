package drinkwater.examples.getstarted2;

import drinkwater.ApplicationBuilder;
import drinkwater.rest.RestComponent;

public class MyAppBuilder extends ApplicationBuilder {
    public IServiceProvider getServiceProvider(){
        return new DefaultServiceProvider(null);
    }

    public void addServices(){

        addService(ISimpleService.class).asRest();
        expose(ISimpleService.class)
                .as(RestComponent.class).withPort(10);

    }


    public static void main(String[] args) {
        MyAppBuilder app = new MyAppBuilder();
        app.expose(ISimpleService.class)
                .as(RestComponent.class).withPort(10).named("test");
    }
}
