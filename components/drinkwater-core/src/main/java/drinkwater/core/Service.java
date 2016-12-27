package drinkwater.core;

import org.apache.camel.impl.DefaultCamelContext;

import javax.enterprise.inject.Vetoed;
import java.util.Properties;

/**
 * Created by A406775 on 23/12/2016.
 */
@Vetoed
public class Service {

    public enum Status { UP, DOWN}

    private String name;

    private DefaultCamelContext context;

    private Properties properties;

    public Status getStatus() {
        if(context.isStarted()){
            return Status.UP;
        }
        return Status.DOWN;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DefaultCamelContext getContext() {
        return context;
    }

    public void setContext(DefaultCamelContext context) {
        this.context = context;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }


}
