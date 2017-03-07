package drinkwater.http.proxy;

import drinkwater.Builder;
import drinkwater.helper.CamelContextHelper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.lang.reflect.Method;

import static drinkwater.rest.RestHelper.restPathFor;

public class HttpProxyServiceBuilder extends Builder {
    public static final String SessionSupport = "sessionSupport:false";
    public static final String FrontEndPoint = "proxy.endpoint";
    public static final String DestintationEndpoint = "destination.endpoint";

    public boolean handlerRegistered = false;

    public void test(){}

    private String startEndpoint(Method method) {
        String restPath = restPathFor(method);
        Boolean useSessionManager = (Boolean)lookupProperty(Boolean.class, SessionSupport);

        String sessionManagerOption = useSessionManager ? "&sessionSupport=true" : "";

        String frontEndpoint = (String)lookupProperty(String.class, FrontEndPoint);


        if (frontEndpoint == null) {
            throw new RuntimeException("could not find proxy and destination endpoint from config");
        }
        if(!restPath.startsWith("/")){
            restPath = "/" + restPath;
        }

        //only one handler registration by port
        String handlersConfig = "";
        if(!handlerRegistered) {
            String handlers = getHandlersForJetty();
            handlersConfig = handlers == null ? "" : "&handlers=" + handlers;
            handlerRegistered = true;
        }


        return "jetty:" + frontEndpoint + restPath + "?matchOnUriPrefix=true&enableMultipartFilter=false"
                + handlersConfig + sessionManagerOption;

    }

    private String endEndPoint(Method method) {
        String restPath = restPathFor(method);
        String destinationEndpoint = (String)lookupProperty(String.class, DestintationEndpoint);

        if (destinationEndpoint == null) {
            throw new RuntimeException("could not find proxy and destination endpoint from config");
        }
        if(!restPath.startsWith("/")){
            restPath = "/" + restPath;
        }

        return "jetty:" + destinationEndpoint  + restPath + "?bridgeEndpoint=true&amp;throwExceptionOnFailure=true";
    }

    @Override
    public RouteDefinition exposeService(RouteBuilder rb, Method method) {
            return rb.from(startEndpoint(method));
    }

    @Override
    public void targetService(RouteDefinition processDefinition, Method method) {
            processDefinition.to(endEndPoint(method));
    }

    public String getHandlersForJetty() {
        String handlers = (String)lookupProperty(String.class, "handlers");
        if (handlers == null) {
            return null;
        }
        String[] handlersArray = handlers.split(",");

        try {
            for (String handler :
                    handlersArray) {
                Object obj = Class.forName(handler).newInstance();
                CamelContextHelper.registerBean(getCamelContext(), handler, obj);
            }
            return handlers;
        } catch (Exception ex) {
            throw new RuntimeException("could not register handler : ", ex);
        }
    }
}
