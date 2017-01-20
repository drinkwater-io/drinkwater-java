package drinkwater.trace;

import java.util.Map;

/**
 * Created by A406775 on 5/01/2017.
 */
public class Payload {
    private Operation operation;

    private Map<String, Object> headers;

    private Object body;

    public Payload(Operation operation, Map<String, Object> headers, Object body) {
        this.operation = operation;
        this.headers = headers;
        this.body = body;
    }

//    public static Payload of(Object... obj) {
//        return new Payload(obj);
//    }

    public static Payload of(Operation method, Map<String, Object> headers, Object body)  {
        return new Payload(method, headers, body);
    }

    /**
     * @deprecated use getters to access the fields
     * @return
     */
    @Deprecated
    public Object[] getTarget(){
        Object[] targets =  new Object[3];
        targets[0] = operation;
        targets[1] = headers;
        targets[2] = body;

        return targets;
    }

    public Operation getOperation() {
        return operation;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }
}
