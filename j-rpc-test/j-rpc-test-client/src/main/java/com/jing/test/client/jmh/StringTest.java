package com.jing.test.client.jmh;

import com.github.jingshouyan.jrpc.base.util.desensitize.JsonMasking;
import com.google.common.collect.Maps;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
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
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class StringTest {

//    @Param({"1000","1000000"})
//    private int count;

    String str2 = "{\"serverInfo\":{\"name\":\"test\",\"version\":\"1.0\",\"host\":\"192.168.81.70\",\"port\":8999,\"startAt\":\"2020-05-08T17:57:36.233\",\"timeout\":5000,\"maxReadBufferBytes\":26214400,\"updatedAt\":\"\",\"monitorInfo\":{\"totalRequest\":0,\"totalCost\":0,\"totalMemory\":268435456,\"freeMemory\":201077640,\"maxMemory\":3797417984,\"osName\":\"Windows 10\",\"totalMemorySize\":0,\"freePhysicalMemorySize\":0,\"usedMemory\":67357816,\"totalThread\":18,\"cpuRatio\":-1.0},\"instance\":\"88ec2470-c0e8-4f78-b77f-c9135225830c\",\"selector\":8,\"worker\":16},\"methodInfos\":[{\"name\":\"ping\",\"remark\":\"\",\"input\":{\"rootType\":\"Object\",\"types\":[]},\"output\":{\"rootType\":\"Rsp<Object>\",\"types\":[{\"type\":\"Rsp<Object>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"Object\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"update\",\"remark\":\"\",\"input\":{\"rootType\":\"UpdateDTO\",\"types\":[{\"type\":\"UpdateDTO\",\"fields\":[{\"name\":\"bean\",\"type\":\"String\",\"annotations\":[\"NotNull\"]},{\"name\":\"type\",\"type\":\"String\",\"annotations\":[\"NotNull\"]},{\"name\":\"data\",\"type\":\"String\",\"annotations\":[\"NotNull\"]}],\"annotations\":[]}]},\"output\":{\"rootType\":\"Rsp<Object>\",\"types\":[{\"type\":\"Rsp<Object>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"Object\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"delete\",\"remark\":\"\",\"input\":{\"rootType\":\"DeleteDTO\",\"types\":[{\"type\":\"DeleteDTO\",\"fields\":[{\"name\":\"bean\",\"type\":\"String\",\"annotations\":[\"NotNull\"]},{\"name\":\"type\",\"type\":\"String\",\"annotations\":[\"NotNull\"]},{\"name\":\"id\",\"type\":\"Object\",\"annotations\":[]},{\"name\":\"ids\",\"type\":\"List<Object>\",\"annotations\":[]}],\"annotations\":[]}]},\"output\":{\"rootType\":\"Rsp<Object>\",\"types\":[{\"type\":\"Rsp<Object>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"Object\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"myMethod\",\"remark\":\"\",\"input\":{\"rootType\":\"Empty\",\"types\":[{\"type\":\"Empty\",\"fields\":[],\"annotations\":[]}]},\"output\":{\"rootType\":\"Rsp<Void>\",\"types\":[{\"type\":\"Rsp<Void>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"Void\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"testMethod\",\"remark\":\"\",\"input\":{\"rootType\":\"List<String>\",\"types\":[]},\"output\":{\"rootType\":\"Rsp<TestBean2<CodeInfo,String,TestBean3>>\",\"types\":[{\"type\":\"Rsp<TestBean2<CodeInfo,String,TestBean3>>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"TestBean2<CodeInfo,String,TestBean3>\",\"annotations\":[]}],\"annotations\":[]},{\"type\":\"TestBean2<CodeInfo,String,TestBean3>\",\"fields\":[{\"name\":\"test\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"List<CodeInfo>\",\"annotations\":[]},{\"name\":\"mapList\",\"type\":\"List<Map<String,String>>\",\"annotations\":[]},{\"name\":\"set\",\"type\":\"Set<TestBean3>\",\"annotations\":[]},{\"name\":\"t\",\"type\":\"CodeInfo\",\"annotations\":[]}],\"annotations\":[]},{\"type\":\"TestBean3\",\"fields\":[{\"name\":\"test\",\"type\":\"String\",\"annotations\":[]}],\"annotations\":[]},{\"type\":\"CodeInfo\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"whoUse\",\"type\":\"String\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"asyncTest\",\"remark\":\"\",\"input\":{\"rootType\":\"String\",\"types\":[]},\"output\":{\"rootType\":\"Rsp<String>\",\"types\":[{\"type\":\"Rsp<String>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"String\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"create\",\"remark\":\"\",\"input\":{\"rootType\":\"CreateDTO\",\"types\":[{\"type\":\"CreateDTO\",\"fields\":[{\"name\":\"bean\",\"type\":\"String\",\"annotations\":[\"NotNull\"]},{\"name\":\"type\",\"type\":\"String\",\"annotations\":[\"NotNull\"]},{\"name\":\"data\",\"type\":\"String\",\"annotations\":[\"NotNull\"]}],\"annotations\":[]}]},\"output\":{\"rootType\":\"Rsp<Object>\",\"types\":[{\"type\":\"Rsp<Object>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"Object\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"asyncErr\",\"remark\":\"\",\"input\":{\"rootType\":\"String\",\"types\":[]},\"output\":{\"rootType\":\"Rsp<String>\",\"types\":[{\"type\":\"Rsp<String>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"String\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"getUserInfo\",\"remark\":\"\",\"input\":{\"rootType\":\"IdQuery\",\"types\":[{\"type\":\"IdQuery\",\"fields\":[{\"name\":\"name\",\"type\":\"String\",\"annotations\":[\"NotNull(message=CODE:200)\",\"Size(min=4,max=20)\"]},{\"name\":\"age\",\"type\":\"int\",\"annotations\":[\"Min(value=5)\",\"Max(value=99)\"]},{\"name\":\"ids\",\"type\":\"List<String>\",\"annotations\":[\"NotNull\",\"Size(min=1,max=100)\"]}],\"annotations\":[]}]},\"output\":{\"rootType\":\"Rsp<List<UserBean>>\",\"types\":[{\"type\":\"Rsp<List<UserBean>>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"List<UserBean>\",\"annotations\":[]}],\"annotations\":[]},{\"type\":\"UserBean\",\"fields\":[{\"name\":\"id\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"name\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"age\",\"type\":\"Integer\",\"annotations\":[]},{\"name\":\"tags\",\"type\":\"List<String>\",\"annotations\":[]},{\"name\":\"nickname\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"createdAt\",\"type\":\"Long\",\"annotations\":[]},{\"name\":\"updatedAt\",\"type\":\"Long\",\"annotations\":[]},{\"name\":\"deletedAt\",\"type\":\"Long\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"traceTest\",\"remark\":\"\",\"input\":{\"rootType\":\"Integer\",\"types\":[]},\"output\":{\"rootType\":\"Rsp<Integer>\",\"types\":[{\"type\":\"Rsp<Integer>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"Integer\",\"annotations\":[]}],\"annotations\":[]}]}},{\"name\":\"traceTest2\",\"remark\":\"\",\"input\":{\"rootType\":\"Integer\",\"types\":[]},\"output\":{\"rootType\":\"Rsp<Integer>\",\"types\":[{\"type\":\"Rsp<Integer>\",\"fields\":[{\"name\":\"code\",\"type\":\"int\",\"annotations\":[]},{\"name\":\"message\",\"type\":\"String\",\"annotations\":[]},{\"name\":\"data\",\"type\":\"Integer\",\"annotations\":[]}],\"annotations\":[]}]}}],\"codeInfos\":[{\"code\":-302,\"message\":\"not allowed\"},{\"code\":-208,\"message\":\"permission denied\"},{\"code\":-207,\"message\":\"bad request\"},{\"code\":-206,\"message\":\"ticket not set\"},{\"code\":-205,\"message\":\"userId not set\"},{\"code\":-204,\"message\":\"param invalid\"},{\"code\":-203,\"message\":\"json parse error\"},{\"code\":-202,\"message\":\"method not found\"},{\"code\":-201,\"message\":\"server error\"},{\"code\":-103,\"message\":\"version server not found\"},{\"code\":-102,\"message\":\"instance not found\"},{\"code\":-101,\"message\":\"server not found\"},{\"code\":-3,\"message\":\"connect timeout\"},{\"code\":-2,\"message\":\"unsupported route mode\"},{\"code\":-1,\"message\":\"client error\"},{\"code\":1,\"message\":\"success\"},{\"code\":200,\"message\":\"name is null\"},{\"code\":201,\"message\":\"就是想返回个错误\"},{\"code\":202,\"message\":\"就是想返回个错误并且带点数据\"}]}";
    String str3 = "{\"t\":{\"code\":100,\"message\":\"1123\"},\"test\":\"123\",\"data\":[{\"code\":100,\"message\":\"1123\"}],\"mapList\":[{\"abc2\":\"eee2\",\"abc\":\"eee\"},{\"2abc\":\"eee\",\"2abc2\":\"eee2\"}],\"set\":[{\"test\":\"testBean3\"}]}";
    String str4 = "{\"name\":\"11111qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq\",\"age\":55,\"message\":\"gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg\"}";
    private char[] chars = ("1234567890" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "****************************************************************************************************" +
            "1234567890").toCharArray();
    String str = new String(chars);

    //    @Benchmark
    public String replace() {
        String str = new String(chars);
        return str.replace("*", "");
    }

    //    @Benchmark
    public String replace2() {
        return str.replace("*", "");
    }

    @Benchmark
    public String newString() {
        return new String(chars);
    }

    @Benchmark
    public String sb() {
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (c != '*') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    //    @Benchmark
    public String mask2() {
        return JsonMasking.DEFAULT.masking(str2);
    }

    //    @Benchmark
    public String mask3() {
        return JsonMasking.DEFAULT.masking(str3);
    }

    //    @Benchmark
    public String mask4() {
        return JsonMasking.DEFAULT.masking(str4);
    }

    @Setup
    public void prepare() {
        Map<String, Integer> map = Maps.newHashMap();
        map.put("message", 33);
        map.put("name", 22);
        JsonMasking.DEFAULT.addSetting(map);
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
