package test.drinkwater.test;

import drinkwater.test.jndi.MockJndiContextFactoryRule;
import org.junit.Rule;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MockInitialContextFactoryTest {

    @Rule
    public MockJndiContextFactoryRule jndiContext = new MockJndiContextFactoryRule();

    @Test
    public void shouldRetrievebeanFromJndi() throws NamingException {

        Context ctx = new InitialContext();
        ctx.bind("hello", "world");
        Object result = ctx.lookup("hello");

        assertThat(result).isEqualTo("world");

    }

    @Test
    public void shouldThrowExceptionbecauseNotBound()  {

        assertThatThrownBy(() -> {
            Context ctx = new InitialContext();
            Object result = ctx.lookup("hello");
        }).isInstanceOf(NamingException.class).hasMessageContaining("hello");

    }
}
