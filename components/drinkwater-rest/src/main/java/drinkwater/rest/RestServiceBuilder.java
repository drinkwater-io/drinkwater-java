package drinkwater.rest;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.Unirest;
import drinkwater.Builder;
import drinkwater.helper.SocketUtils;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestPropertyDefinition;

import java.io.IOException;
import java.lang.reflect.Method;

import static drinkwater.rest.RestHelper.*;
import static java.util.Collections.singletonList;

public class RestServiceBuilder extends Builder {

    public static final String REST_HOST_KEY = "drinkwater.rest.host:0.0.0.0";
    public static final String REST_PORT_KEY = "drinkwater.rest.port";
    public static final String REST_CONTEXT_KEY = "drinkwater.rest.contextpath:";
    public static final String CORS_CONTEXT_KEY = "cors.Access-Control-Allow-Headers:";

    @Override
    public void start() {
        Unirest.setObjectMapper(new UnirestJacksonObjectMapper());
    }

    @Override
    public void stop() {
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beforeExposeService(RouteBuilder rb){

        String port =  addProperty(REST_PORT_KEY, SocketUtils.freePort() + "" );

        String allowedCorsHeaders =  (String)lookupProperty(String.class, CORS_CONTEXT_KEY);
        String host = (String) lookupProperty(String.class, REST_HOST_KEY);
        String context = (String) lookupProperty(String.class, REST_CONTEXT_KEY +  getName());

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

    @Override
    public RouteDefinition exposeService(RouteBuilder rb, Method method){
        HttpMethod httpMethod = httpMethodFor(method);

        String restPath = restPathFor(method);

        RestDefinition restDefinition =
                toRestdefinition(rb, method, httpMethod, restPath);

        RouteDefinition routeDefinition = restDefinition.route();

        return routeDefinition;
    }

    @Override
    public void targetService(RouteDefinition processDefinition, Method method){
        String camelMethod = camelMethodBuilder(method);

        processDefinition.bean(getBean(), camelMethod);
    }
}
