package drinkwater.test.jndi;

import org.apache.camel.util.jndi.JndiContext;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.naming.Context;

public class MockJndiContextFactoryRule implements TestRule {

    private final Context context ;

    public MockJndiContextFactoryRule() {

        try {
            this.context = new JndiContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MockInitialContextFactory.class.getName());
                MockInitialContextFactory.setCurrentContext(context);
                try {
                    base.evaluate();
                } finally {
                    System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
                    MockInitialContextFactory.clearCurrentContext();
                }
            }
        };
    }

}


