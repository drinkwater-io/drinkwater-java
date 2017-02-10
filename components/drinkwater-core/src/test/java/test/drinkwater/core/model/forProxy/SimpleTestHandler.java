package test.drinkwater.core.model.forProxy;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SimpleTestHandler extends AbstractHandler {

    private Logger logger = LoggerFactory.getLogger(SimpleTestHandler.class);
    public static int increment = 0;
    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {

        try {
            if (httpServletRequest.getQueryString() != null) {
                if (httpServletRequest.getQueryString().contains("increment")) {
                    increment++;
                }
            }
        }
        catch(Exception ex){

            logger.error(ex.getMessage());
            throw new ServletException("exception occured in SimpleTestHandler ", ex);
        }
    }
}
