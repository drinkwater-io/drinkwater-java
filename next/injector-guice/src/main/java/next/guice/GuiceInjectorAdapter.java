package next.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import next.api.IInjectorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by A406775 on 24/03/2017.
 */
public class GuiceInjectorAdapter implements IInjectorAdapter {

    public static Injector guiceInjector;

    public GuiceInjectorAdapter(Module... modules) {

        List<Module> innerModules = new ArrayList<>(Arrays.asList(modules));
        innerModules.add(new DrinkWaterModule());
        guiceInjector = Guice.createInjector(innerModules);
    }

    @Override
    public <T> T get(Class<? extends T> clazz) {
        return guiceInjector.getInstance(clazz);
    }
}
