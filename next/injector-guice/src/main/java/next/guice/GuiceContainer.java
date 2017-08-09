package next.guice;

import next.api.IContainer;

import static next.guice.GuiceInjectorAdapter.guiceInjector;

/**
 * Created by A406775 on 24/03/2017.
 */
public class GuiceContainer implements IContainer {

   public <T> T get(Class<? extends T> clazz){
       return guiceInjector.getInstance(clazz);
   }
}
