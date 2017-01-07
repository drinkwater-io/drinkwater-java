package drinkwater;

/**
 * Created by A406775 on 6/01/2017.
 */
public interface IRoutingBuilder {
    IRoutingBuilder useHeader(String routingheader);

    IRoutingBuilder route(String headerValue, String serviceName);
}
