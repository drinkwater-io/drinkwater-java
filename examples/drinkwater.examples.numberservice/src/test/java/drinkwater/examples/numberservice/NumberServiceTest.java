package drinkwater.examples.numberservice;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class NumberServiceTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NumberServiceTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( NumberServiceTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        String filePath = "c:/temp/numbers.txt";
        long startTime = System.nanoTime();

        NumberService svc = new NumberService();
        svc.setNumberFormatter(new NumberFormatter());
        svc.setNumberRepository(new NumberRepository());

        svc.saveNumber(filePath, 10);
        svc.saveNumber(filePath, 20);
        svc.saveNumber(filePath, 30);

        List<String> numbers = svc.getNumberList(filePath);

        assertEquals(3, numbers.size());

        svc.clear(filePath);

        long estimatedTime = System.nanoTime() - startTime;

        System.out.println("test took " + TimeUnit.NANOSECONDS.toMillis(estimatedTime) + " millis");
    }
}
