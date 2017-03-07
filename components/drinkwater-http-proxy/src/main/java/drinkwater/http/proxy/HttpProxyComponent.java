package drinkwater.http.proxy;

import drinkwater.IBuilderProvider;

public class HttpProxyComponent implements IBuilderProvider<HttpProxyServiceBuilder>{

    HttpProxyServiceBuilder _curBuilder = new HttpProxyServiceBuilder();

    @Override
    public HttpProxyServiceBuilder getBuilder() {
        return _curBuilder;
    }





}
