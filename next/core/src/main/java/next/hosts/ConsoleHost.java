package next.hosts;

import next.api.IInjectorAdapter;
import next.hosts.exceptions.NextApplicationConfigurationException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by A406775 on 24/03/2017.
 */
public class ConsoleHost {

    public static void run(Class startupClass, IInjectorAdapter injector) throws Exception{

        try {
            injector.start();
            Method configureMethod = getConfigureMethod(startupClass);
            Object obj = injector.get(startupClass);
            configureMethod.invoke(obj);

        }finally{
            injector.stop();
        }
    }

    private static Method getConfigureMethod(Class startupClass) throws NextApplicationConfigurationException {
        Method[] method = startupClass.getMethods();

        List<Method> configureMethods = Arrays
                .stream(method)
                .filter(m -> m.getName() == "configure")
                .collect(Collectors.toList());

        if(configureMethods.size() > 1){
            throw new NextApplicationConfigurationException("you must provide at most one 'configure' method");
        }

        return configureMethods.get(0);
    }
}
