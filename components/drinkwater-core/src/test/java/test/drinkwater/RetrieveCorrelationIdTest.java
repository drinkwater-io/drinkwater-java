package test.drinkwater;

import drinkwater.helper.reflect.ReflectHelper;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.slf4j.MDC;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class RetrieveCorrelationIdTest {

    @Test
    public void testCamel() throws Exception {
        DefaultCamelContext ctx = new DefaultCamelContext();
        try {
            ctx.setUseMDCLogging(true);
            ctx.addRoutes(createRouteBuilder());
            ctx.start();
            Object body = ctx.createProducerTemplate().requestBody("direct:a", "text in body");
            assertEquals("TEXT IN BODY", body);
        } finally {
            ctx.stop();
        }
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                IEcho proxy = ReflectHelper.simpleProxy(IEcho.class, new Proxybean(getContext()));

                from("direct:a").routeId("route-a")
                        .setHeader("CUSTOM-CORRELATION-ID", constant("correlationIdsetInHeader"))
                        .process(exchange -> MDC.put("CUSTOM-HEADER-MDC", "correlationIdsetWithMDC"))
                        .bean(proxy, "echo(${body})");

                from("direct:b").routeId("route-b")
                        .process(exchange -> {
                            String customHeader = (String) exchange.getIn().getHeader("CUSTOM-CORRELATION-ID");
                            String mdcHeader = MDC.get("CUSTOM-HEADER-MDC");
                            assertEquals(customHeader, mdcHeader);
                            exchange.getIn().setBody(((String)exchange.getIn().getBody()).toUpperCase());
                        })
                        .to("mock:result");
            }
        };
    }

    class Proxybean implements InvocationHandler {
        CamelContext ctx;
        Proxybean(CamelContext ctx) {
            this.ctx = ctx;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("echo")) {
                // how to get the CUSTOM-CORRELATION-ID here ????µ
                // only possible with MDC ?
                String mdcHeader = MDC.get("CUSTOME-HEADER-MDC");
                String result =
                        (String) ctx.createProducerTemplate()
                                .requestBodyAndHeader("direct:b", args[0], "CUSTOME-HEADER", mdcHeader);
                return result;
            }
            return null;
        }
    }
}
