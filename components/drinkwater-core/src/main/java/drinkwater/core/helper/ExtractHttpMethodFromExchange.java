package drinkwater.core.helper;

import drinkwater.trace.Operation;
import org.apache.camel.Exchange;

public class ExtractHttpMethodFromExchange {

    public Operation extractInfo(Exchange exchange){
        String info = (String) exchange.getIn().getHeader("CamelHttpUri");
        return Operation.of(info);
    }
}
