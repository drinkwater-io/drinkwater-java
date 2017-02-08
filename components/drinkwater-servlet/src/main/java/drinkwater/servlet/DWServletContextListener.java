package drinkwater.servlet;

import drinkwater.ApplicationBuilder;
import drinkwater.core.DrinkWaterApplication;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by A406775 on 30/12/2016.
 */
public final class DWServletContextListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public static DrinkWaterApplication instance;

    public DrinkWaterApplication drinkWaterApplication;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("bok !.... Initializing DrinkWaterApplication through servlet startup");

        Map<String, Object> initParams = extractInitParameters(servletContextEvent);

        String serviceBuilder = (String) initParams.get("applicationBuilder");

        try {
            ApplicationBuilder builder = getServiceConfigurationBuilder(initParams, serviceBuilder);

            DrinkWaterApplication application =
                    DrinkWaterApplication.create();

            instance = application;
            drinkWaterApplication = application;

            application.addServiceBuilder(builder);
            application.start();

        } catch (Exception ex) {
            logger.error("COULD NOT START DrinkWater APPLICATION " + ex.getMessage());
            throw new RuntimeException(ex);
        }

    }

    private static ApplicationBuilder getServiceConfigurationBuilder(Map<String, Object> initParams, String serviceBuilder) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        ApplicationBuilder builder = (ApplicationBuilder)
                Class.forName(serviceBuilder).newInstance();

        List<Tuple2<String, Object>> serviceBuilderProperties =
                List.ofAll(initParams.keySet())
                        .filter(k -> k.startsWith("applicationBuilder."))
                        .map(k -> Tuple.of(k.replace("applicationBuilder.", ""), initParams.get(k)))
                        .toList();

        //TODO use different kind of injection here ? with  annotation or injectionstrategy
        for (Tuple2<String, Object> tuple :
                serviceBuilderProperties) {
            builder.getClass().getField(tuple._1)
                    .set(builder, tuple._2);
        }
        return builder;
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
