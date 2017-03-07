package drinkwater.feature.multipart;

import drinkwater.ComponentBuilder;
import drinkwater.Feature;
import drinkwater.FeatureBuilder;
import drinkwater.helper.json.CustomJacksonObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;

import java.io.InputStream;
import java.lang.reflect.Method;

public class MultiPartFeature implements Feature, FeatureBuilder<MultiPartFeature> {
    @Override
    public void afterServiceExposed(RouteDefinition routeDefinition, Method method, ComponentBuilder componentBuilder) {
        //TODO create own process
        if (isMultipartBody(method)) {
            routeDefinition.process(new FileUploadProcessor());
        }
    }

    @Override
    public void afterServiceTargeted(RouteDefinition routeDefinition, Method method,ComponentBuilder componentBuilder) {
        if (isMultipartBody(method) && hasObjectReturnType(method)) {
            routeDefinition.process(exchange -> {
                CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
                String s = mapper.writeValueAsString(exchange.getIn().getBody());
                exchange.getIn().setBody(s);
            });
        }
    }



    private static boolean isMultipartBody(Method method) {

        if (method.getParameters().length > 0) {
            Class type = method.getParameters()[0].getType();
            if (type.isAssignableFrom(InputStream.class)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasObjectReturnType(Method method) {
        if (method.getReturnType() == String.class) {
            return false;
        }
        return method.getReturnType() != Void.class;
    }

    @Override
    public MultiPartFeature getFeature() {
        return new MultiPartFeature();
    }

    public static MultiPartFeature multipart(){
        return new MultiPartFeature();
    }
}
