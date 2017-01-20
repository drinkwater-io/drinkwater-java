package drinkwater.trace;

import java.lang.reflect.Method;

public class Operation {

    String operationName;

    Method method;

    public Operation(String operationName) {
        this.operationName = operationName;
    }

    public Operation(Method method) {
        this.method = method;
    }

    public static Operation of(Method m){
        return new Operation(m);
    }

    public static Operation of(String operationName){
        return new Operation(operationName);
    }

    public Object either(){
        if(method == null){
            return operationName;
        }
        return method;
    }

    public boolean isMethod(){
        return method != null;
    }

    @Override
    public String toString() {
        String prefix = "";
        if(isMethod()){
            return prefix + method.getDeclaringClass().getName() + "." + method.getName();
        }
        return prefix + operationName;
    }
}
