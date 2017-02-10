package drinkwater.servlet;

import drinkwater.core.DrinkWaterApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import static drinkwater.ApplicationOptionsBuilder.options;

/**
 * Created by A406775 on 30/12/2016.
 */
public final class DrinkWaterServletContextListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static DrinkWaterApplication instance;

    public DrinkWaterApplication drinkWaterApplication;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Map<String, Object> initParams = extractInitParameters(servletContextEvent);

        try {
            String serviceBuilder = (String) initParams.get("drinkwater.applicationBuilder");
            String applicationName = (String) initParams.get("drinkwater.applicationName");

            DrinkWaterApplication application =
                    DrinkWaterApplication.create(applicationName, options().use(Class.forName(serviceBuilder)));

            instance = application;
            drinkWaterApplication = application;

            application.start();

        } catch (Exception ex) {
            logger.error("COULD NOT START DrinkWater APPLICATION " + ex.getMessage());
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("stoping DrinkWaterApplication through servlet contextDestroyed");

        if (drinkWaterApplication != null) {
            drinkWaterApplication.stop();
        }

        logger.info("DrinkWaterApplication stopped .....  bok !");
    }

    private Map<String, Object> extractInitParameters(ServletContextEvent sce) {
        // configure CamelContext with the init parameter
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Enumeration<?> names = sce.getServletContext().getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = sce.getServletContext().getInitParameter(name);


            map.put(name, value);
        }
        return map;
    }
//    public static Map<String, String> mapOf(Tuple2<String, String>){
//
//    }


}
