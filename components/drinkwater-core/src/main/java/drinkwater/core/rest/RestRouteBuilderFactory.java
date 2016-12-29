package drinkwater.core.rest;

import drinkwater.core.BeanFactory;
import drinkwater.core.DrinkWaterApplication;
import javaslang.collection.List;
import drinkwater.core.ServiceConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.rest.RestBindingMode;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Created by A406775 on 23/12/2016.
 */
public class RestRouteBuilderFactory {

    @Inject
    @ServiceComponent()
    Instance<Object> _components;

    //FIXME to many params
    public RouteBuilder createRestRouteBuilder(DrinkWaterApplication app, PropertiesComponent pc, ServiceConfiguration config) {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //FIXME get all this from the config
                restConfiguration().component("jetty")
                        .host("localhost")
                        .port("8889")
                        .contextPath(config.getServiceClass().getSimpleName().toLowerCase())
                        .bindingMode(RestBindingMode.json);

                Object bean = BeanFactory.createBean(app, pc, config);
                RestRouteBuilder.buildGetRoutemappings(this, bean);
                RestRouteBuilder.buildPostRoutemappings(this, bean);

//                List.ofAll(_components)
//                        .map(obj -> RestRouteBuilder.buildGetRoutemappings(this, obj));
//                List.ofAll(_components)
//                        .flatMap(obj -> RestRouteBuilder.buildPostRoutemappings(this, obj));
            }
        };
    }
}
