package com.jing.test.client.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author jingshouyan
 * #date 2020/5/8 17:36
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 2, time = 10)
@Threads(40)
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class StringTest {

//    @Param({"1000","1000000"})
//    private int count;

    private char[] chars = ("1234567890" +
            "****************************************************************************************************" +
            "1234567890").toCharArray();
    String str = new String(chars);
    @Benchmark
    public String replace() {
        String str = new String(chars);
        return str.replace("*","");
    }
    @Benchmark
    public String replace2() {
        return str.replace("*","");
    }
    @Benchmark
    public String sb() {
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (c!='*'){
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws RunnerException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-hhmm");
        String time = LocalDateTime.now().format(dtf);
        Options options = new OptionsBuilder()
                .include(StringTest.class.getSimpleName())
                .output("./" + time + "-string.log")
                .build();
        new Runner(options).run();
    }
}
