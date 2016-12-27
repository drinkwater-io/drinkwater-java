package drinkwater.core;

import org.apache.camel.*;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.support.LifecycleStrategySupport;

import javax.inject.Inject;
import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

/**
 * Created by A406775 on 23/12/2016.
 */
public class MasterContextLifecycle extends LifecycleStrategySupport {

    @Inject
    private Logger logger ;


    @Override
    public void onContextStart(CamelContext context) throws VetoCamelContextStartException {

        logger.fine(context.toString());
    }


    @Override
    public void onContextStop(CamelContext context) {
        logger.fine(context.toString());
    }

    @Override
    public void onComponentAdd(String name, Component component) {
        logger.fine(component.toString());
    }

    @Override
    public void onComponentRemove(String name, Component component) {
        logger.fine(component.toString());
    }

    @Override
    public void onEndpointAdd(Endpoint endpoint) {
        logger.fine(endpoint.toString());
    }

    @Override
    public void onEndpointRemove(Endpoint endpoint) {
        logger.fine(endpoint.toString());
    }


    @Override
    public void onRoutesAdd(Collection<Route> routes) {
        logger.fine(routes.toString());
    }

    @Override
    public void onRoutesRemove(Collection<Route> routes) {
        logger.fine(routes.toString());
    }

    @Override
    public void onRouteContextCreate(RouteContext routeContext) {
        logger.fine(routeContext.toString());
    }

    @Override
    public void onErrorHandlerAdd(RouteContext routeContext, Processor errorHandler, ErrorHandlerFactory errorHandlerBuilder) {
        logger.fine(routeContext.toString());
    }

    @Override
    public void onErrorHandlerRemove(RouteContext routeContext, Processor errorHandler, ErrorHandlerFactory errorHandlerBuilder) {
        logger.fine(routeContext.toString());
    }

    @Override
    public void onThreadPoolAdd(CamelContext camelContext, ThreadPoolExecutor threadPool, String id,
                                String sourceId, String routeId, String threadPoolProfileId) {
        logger.fine(camelContext.toString());
    }

    @Override
    public void onThreadPoolRemove(CamelContext camelContext, ThreadPoolExecutor threadPool) {
        logger.fine(camelContext.toString());
    }


}
