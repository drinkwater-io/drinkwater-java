package drinkwater.core.rest;

import javaslang.collection.List;
import drinkwater.core.ServiceConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Created by A406775 on 23/12/2016.
 */
public class RestRouteBuilderFactory{

    @Inject
    @ServiceComponent()
    Instance<Object> _components;

    public RouteBuilder createRestRouteBuilder(ServiceConfiguration config){

        return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    restConfiguration().component("jetty")
                                        .host("localhost")
                                        .port("8889")
                            .contextPath(config.getContextPath())
                                        .bindingMode(RestBindingMode.json);
                    List.ofAll(_components)
                            .map(obj -> RestRouteBuilder.buildGetRoutemappings(this, obj));
                    List.ofAll(_components)
                            .flatMap(obj -> RestRouteBuilder.buildPostRoutemappings(this, obj));
                }
            };
    }
}
