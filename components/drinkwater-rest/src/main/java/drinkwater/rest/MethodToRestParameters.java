package drinkwater.rest;

import com.mashape.unirest.http.HttpMethod;
import javaslang.collection.List;
import org.apache.camel.Exchange;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

import static drinkwater.helper.reflect.ReflectHelper.returnsVoid;
import static drinkwater.rest.RestHelper.httpMethodFor;

/**
 * Created by A406775 on 30/12/2016.
 */
public class MethodToRestParameters {

    private Method method;

    private boolean hasReturn;

    private boolean hasBody = false;

    private List<String> headerNames = List.empty();

    public MethodToRestParameters(Method method) {
        this.method = method;
        init();
    }

    private void init() {
        HttpMethod httpMethod = httpMethodFor(method);
        List<Parameter> parameterInfos = List.of(method.getParameters());

        hasReturn = returnsVoid(method);

        if (parameterInfos.size() == 0) {
            return;
        }

        if (httpMethod == HttpMethod.GET) {
            hasBody = false;
        } else if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.DELETE || httpMethod == HttpMethod.PUT) {
            if (parameterInfos.size() > 0) {
                hasBody = true;
            }

        } else {
            throw new RuntimeException("come back here : MethodToRestParameters.init()");
        }

        if (hasBody) { // first parameter of the method will be assigned with the body content
            headerNames = parameterInfos.tail().map(p -> mapName(p)).toList();
        } else {

            headerNames = parameterInfos.map(p -> mapName(p)).toList();
        }

    }

    private static String mapName(Parameter p){
        String name;
        if(p.getClass().isAssignableFrom(Exchange.class)){
            name = "exchange";
        }else {
            name =  p.getName();
        }
        return name;
    }


    public boolean hasBody() {
        return hasBody;
    }

    public boolean hasHeaders() {
        return headerNames.size() > 0;
    }

    public boolean hasReturn() {
        return hasReturn;
    }

    public java.util.List<String> getHeaders() {
        return headerNames.toJavaList();
    }

    public String exchangeToBean() {

        String params = "";

        java.util.List<String> methodParams = new ArrayList<>();
        //if has bod, first parm will be the body
        if (hasBody) {
            methodParams.add("${body}");
        }
        headerNames.forEach(name -> {
            if("exchange".equals(name)){
                methodParams.add("${exchange}");
            }
            methodParams.add("${header." + name + "}");
        });

        params = List.ofAll(methodParams).mkString(",");

        return method.getName() + "(" + params + ")";
    }


}
