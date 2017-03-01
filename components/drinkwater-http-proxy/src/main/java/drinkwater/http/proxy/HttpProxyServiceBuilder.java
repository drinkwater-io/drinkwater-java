package drinkwater.http.proxy;

import drinkwater.Builder;
import org.apache.camel.builder.RouteBuilder;

import java.lang.reflect.Method;

public class HttpProxyServiceBuilder extends Builder {
    public static final String SessionSupport = "sessionSupport";
    public static final String FrontEndPoint = "proxy.endpoint";
    public static final String DestintationEndpoint = "destination.endpoint";


    private String startEndpoint() {
        Boolean useSessionManager = (Boolean)lookupProperty(Boolean.class, "sessionSupport", false);

        String sessionManagerOption = useSessionManager ? "&sessionSupport=true" : "";

        String frontEndpoint = (String)lookupProperty(String.class, FrontEndPoint, "");


        if (frontEndpoint == null) {
            throw new RuntimeException("could not find proxy and destination endpoint from config");
        }

        return "jetty:" + frontEndpoint + "?matchOnUriPrefix=true&enableMultipartFilter=false"
                + sessionManagerOption;

    }

    private String endEndPoint() {
        String destinationEndpoint = (String)lookupProperty(String.class, DestintationEndpoint, "");

        if (destinationEndpoint == null) {
            throw new RuntimeException("could not find proxy and destination endpoint from config");
        }

        return "jetty:" + destinationEndpoint + "?bridgeEndpoint=true&amp;throwExceptionOnFailure=true";
    }

    public void configureRouteBuilder(RouteBuilder rb) {
        rb.from(startEndpoint()).to(endEndPoint());
    }

    public void configureMethodEndpoint(RouteBuilder rb, Method method) {
        // not based on method
    }

}
