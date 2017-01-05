package drinkwater;

/**
 * Created by A406775 on 3/01/2017.
 */
public class DrinkWaterConstants {

    public static String MetricsOperationTimer = "DW-MetricsOperationTimer";

    public static String BeanOperationName = "DW-BeanOperationName";

    public static String FlowCorrelationIDKey = "DW-FlowCorrelationId";

    public static String ServerReceivedCorrelationIDKey = "DW-ServerReceivedCorrelationID";

    public static String ROUTE_CheckFlowIDHeader = "direct:checkFlowHeader";

    public static String ROUTE_serverReceivedEvent = "direct:serverReceivedEvent";

    public static String ROUTE_serverSentEvent = "direct:serverSentEvent";


    public static String ROUTE_trace = "vm:trace";

}
