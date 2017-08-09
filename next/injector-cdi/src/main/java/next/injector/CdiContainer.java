package next.injector;

import next.api.IContainer;

import static next.injector.CdiInjectorAdpater.cdiContainer;

/**
 * Created by A406775 on 24/03/2017.
 */
public class CdiContainer implements IContainer {
    @Override
    public <T> T get(Class<? extends T> clazz) {
        return cdiContainer.select(clazz).get();
    }
}
