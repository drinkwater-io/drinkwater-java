package testit.hosts;

import next.api.IContainer;
import next.api.INextApplicationBuilder;

import javax.inject.Inject;

/**
 * Created by A406775 on 24/03/2017.
 */
public class Startup {

    @Inject
    INextApplicationBuilder applicationBuilder;

    @Inject
    IContainer container;

    public void configure(){


        System.out.println("in Startup applicationBuilder " + applicationBuilder);
        System.out.println("in Startup container " + container);
        IContainer c = container.get(IContainer.class);
        NextSimpleService e = c.get(NextSimpleService.class);
        System.out.println(e.ping("hello"));
        e = c.get(NextSimpleService.class);
        System.out.println(e.ping("hello2"));

    }
}
