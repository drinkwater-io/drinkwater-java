package drinkwater;

import org.apache.camel.builder.RouteBuilder;

import java.lang.reflect.Method;

public class QuartzServiceBuilder extends Builder {

    @Override
    public void configureRouteBuilder(RouteBuilder rb) {

    }

    public void configureMethodEndpoint(RouteBuilder rb, Method method) {
        String groupName = (String) lookupProperty(Boolean.class, "groupName", getName());
        String timerName = (String) lookupProperty(Boolean.class, "timerName", method.getName());
        String cronExpression = (String) lookupProperty(Boolean.class, "cronexpression", true);
        long interval = (long) lookupProperty(Long.class, "repeat", true);

        String enpoint = "quartz2://" + groupName + "/" +
                timerName + "?fireNow=true";

        if (cronExpression == null) {
            cronExpression = cronExpression.replaceAll(" ", "+");
            enpoint += "&cron=" + cronExpression;

        } else {
            enpoint += "&trigger.repeatInterval=" + interval;
        }

        //camel needs + instead of white space

        //TODO : check correctness of expression see camel doc here the job must have only one method !!!!
        rb.from(enpoint).bean(getBean());

    }

//    public static RoutesBuilder mapCronRoutes(String groupName, DrinkWaterApplication app, Service service) {
//        return new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//
//                boolean jobActive = service.safeLookupProperty(Boolean.class, "job.activated", true);
//
//                if (jobActive) {
//
//                    Object bean = BeanFactory.createBean(app, service.getConfiguration(), service);
//
//                    String cronExpression = service.getConfiguration().getCronExpression();
//                    long interval = service.getConfiguration().getRepeatInterval();
//
//                    cronExpression =
//                            service.lookupProperty(service.getConfiguration().getCronExpression() + ":" + cronExpression);
//
//                    interval = service.safeLookupProperty(Long.class, "job.interval", interval);
//
//                    String enpoint = "quartz2://" + groupName + "/" +
//                            service.getConfiguration().getServiceName() + "?fireNow=true";
//
//                    if (cronExpression == null) {
//                        cronExpression = cronExpression.replaceAll(" ", "+");
//                        enpoint += "&cron=" + cronExpression;
//
//                    } else {
//                        enpoint += "&trigger.repeatInterval=" + interval;
//                    }
//
//                    //camel needs + instead of white space
//
//                    //TODO : check correctness of expression see camel doc here the job must have only one method !!!!
//                    from(enpoint).bean(bean);
//                }
//
//            }
//        };
//    }
}
