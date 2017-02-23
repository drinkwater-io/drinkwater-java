package drinkwater.examples.getstarted2;

import drinkwater.ApplicationBuilder;
import drinkwater.rest.RestComponent;

public class MyAppBuilder extends ApplicationBuilder {
    public IServiceProvider getServiceProvider(){
        return new DefaultServiceProvider(null);
    }

    public void addServices(){
        addService(ISimpleService.class).asRest();
    }


    public static void main(String[] args) {
        MyAppBuilder app = new MyAppBuilder();
//        IRestServiceBuilder sb = app.use(ISimpleService.class)
//                .as(RestComponent.class).getBuilder().withPort(10);
        app.use(ISimpleService.class)
                .asBuilder(RestComponent.class).withPort(10);


//        sb.withPort(10);
    }
}
