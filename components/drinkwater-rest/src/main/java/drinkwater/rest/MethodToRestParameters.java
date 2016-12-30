package drinkwater.rest;

import com.mashape.unirest.http.HttpMethod;
import javaslang.collection.List;

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
        List<Parameter> parameterInfos = javaslang.collection.List.of(method.getParameters());
        hasReturn = returnsVoid(method);

        if (parameterInfos.size() == 0) {
            return;
        }

        if (httpMethod == HttpMethod.GET) {
            headerNames = parameterInfos.map(p -> p.getName()).toList();
        } else if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.DELETE || httpMethod == HttpMethod.PUT) {
            if (parameterInfos.size() > 0) {
                hasBody = true;
            }
            headerNames = parameterInfos.tail().map(p -> p.getName()).toList();
        } else {
            throw new RuntimeException("come back here : MethodToRestParameters.init()");
        }

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
        if (hasBody) {
            methodParams.add("${body}");
        }
        headerNames.forEach(name -> {
            methodParams.add("${header." + name + "}");
        });

        params = List.ofAll(methodParams).mkString(",");

        return method.getName() + "(" + params + ")";
    }


}
