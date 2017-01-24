package drinkwater.test;

import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.servletunit.ServletRunner;
import org.junit.After;
import org.junit.Before;

import java.io.InputStream;

/**
 * Base class for unit testing.
 */
public abstract class ServletUnitTest {
    public static final String CONTEXT = "/mytestcontext";
    public static final String CONTEXT_URL = "http://localhost/mytestcontext";
    protected ServletRunner sr;

    @Before
    public void setUp() throws Exception {
        String configuration = getConfiguration();
        InputStream is = this.getClass().getResourceAsStream(configuration);
        sr = new ServletRunner(is, CONTEXT);

        HttpUnitOptions.setExceptionsThrownOnErrorStatus(true);
    }

    @After
    public void tearDown() throws Exception {
        if (sr != null) {
            sr.shutDown();
        }
    }

    /**
     * @return The web.xml to use for testing.
     */
    protected abstract String getConfiguration();

}
