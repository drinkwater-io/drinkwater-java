package drinkwater.rest;

import com.mashape.unirest.http.HttpMethod;
import drinkwater.Builder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestPropertyDefinition;

import java.lang.reflect.Method;

import static drinkwater.rest.RestHelper.*;
import static java.util.Collections.singletonList;

public class RestServiceBuilder extends Builder {

    public static final String REST_HOST_KEY = "drinkwater.rest.host";
    public static final String REST_PORT_KEY = "drinkwater.rest.port";
    public static final String REST_CONTEXT_KEY = "drinkwater.rest.contextpath";
    public static final String CORS_CONTEXT_KEY = "cors.Access-Control-Allow-Headers";
    public static final String REST_SINK_PORT_KEY = "routing.sink.port";


    public void configureRouteBuilder(RouteBuilder rb) {

        String allowedCorsHeaders = (String) lookupProperty(String.class, CORS_CONTEXT_KEY, "");
        String host = (String) lookupProperty(String.class, REST_HOST_KEY, "");
        String context = (String) lookupProperty(String.class, REST_CONTEXT_KEY, getName());
        int port = (Integer) lookupProperty(Integer.class, REST_PORT_KEY, "");
        RestPropertyDefinition corsAllowedHeaders = new RestPropertyDefinition();
        corsAllowedHeaders.setKey("Access-Control-Allow-Headers");
        corsAllowedHeaders.setValue(allowedCorsHeaders);

        // builder.getContext().getDataFormats();
        RestConfigurationDefinition restConfig =
                rb.restConfiguration()
                        .component("jetty")
                        .enableCORS(true)
                        .scheme("http")
                        .host(host)
                        .port(port)
                        .contextPath(context)
                        .bindingMode(RestBindingMode.json)
                        .jsonDataFormat("json-drinkwater");

        restConfig.setCorsHeaders(singletonList(corsAllowedHeaders));
    }

    public void configureMethodEndpoint(RouteBuilder rb, Method method) {
        HttpMethod httpMethod = httpMethodFor(method);

        String restPath = restPathFor(method);

        RestDefinition restDefinition =
                toRestdefinition(rb, method, httpMethod, restPath);

        String camelMethod = camelMethodBuilder(method);

        ProcessorDefinition routeDefinition = restDefinition.route();


        routeDefinition = routeDefinition.bean(getBean(), camelMethod);

    }
}
