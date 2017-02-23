package test.drinkwater.helper;

import org.openjdk.jmh.annotations.Benchmark;

import static drinkwater.helper.StringUtils.trimEnclosingQuotes;

public class BenchMarkTest {
    static {
        System.setProperty("jmh.ignoreLock", "true");
    }

    @Benchmark
    public String measureName() {
        return trimEnclosingQuotes("\"hello\"");
    }
}
