package drinkwater.core;

import drinkwater.ComponentBuilder;
import drinkwater.Feature;
import drinkwater.helper.reflect.ReflectHelper;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;
import java.util.List;

public class DrinkWaterRouteBuilder extends RouteBuilder {

    private ComponentBuilder componentBuilder;

    public DrinkWaterRouteBuilder(ComponentBuilder componentBuilder) {
//        super(context);
        this.componentBuilder = componentBuilder;

    }

    @Override
    public void configure() throws Exception {


        Method[] methods = ReflectHelper.getPublicDeclaredMethods(componentBuilder.getBuilder().getServiceClass());

        List<Feature> features = componentBuilder.getBuilder().getFeatures();

        componentBuilder.getBuilder().beforeExposeService(this);

        for (Method method :
                methods) {
            RouteDefinition pd = componentBuilder.getBuilder().exposeService(this, method);
            if(pd != null) {

                features.forEach(f -> f.afterServiceExposed(pd, method));

                componentBuilder.getBuilder().targetService(pd, method);

                features.forEach(f -> f.afterServiceTargeted(pd, method));
            }

        }
    }
}
