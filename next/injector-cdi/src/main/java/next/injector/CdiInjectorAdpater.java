package next.injector;

import next.api.IInjectorAdapter;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Created by A406775 on 24/03/2017.
 */
public class CdiInjectorAdpater implements IInjectorAdapter{

    public static WeldContainer cdiContainer;

    public void start(){
        Weld weld = new Weld();
        cdiContainer = weld.initialize();
    }

    public void stop(){
        if(cdiContainer != null) {
            cdiContainer.shutdown();
        }
    }

    @Override
    public <T> T get(Class<? extends T> clazz) {
        return cdiContainer.select(clazz).get();
    }
}
