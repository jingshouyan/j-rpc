//package com.jing.test.client.jmh;
//
//import com.github.jingshouyan.jrpc.base.bean.Rsp;
//import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
//import com.github.jingshouyan.jrpc.client.JrpcClient;
//import com.github.jingshouyan.jrpc.client.Request;
//import com.github.jingshouyan.jrpc.client.config.ClientConfig;
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.RunnerException;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author jingshouyan
// * #date 2019/2/25 18:58
// */
//@BenchmarkMode(Mode.Throughput)
//@Warmup(iterations = 1, time = 5)
//@Measurement(iterations = 2, time = 10)
//@Threads(40)
//@Fork(1)
//@OutputTimeUnit(TimeUnit.SECONDS)
//@State(Scope.Thread)
//public class PingTest {
//
//    private static String host = "127.0.0.1";
//    private static int port = 8999;
//    private Jrpc.Client client = ClientUtil.client(host, port);
//
//    private static ClientConfig clientConfig = new ClientConfig();
//
//    static {
//        clientConfig.setZkHost(host);
////        clientConfig.setPoolMinIdle(100);
////        clientConfig.setPoolMaxTotal(500);
////        clientConfig.setPoolMaxIdle(200);
//    }
//
//    private static JrpcClient jrpcClient = new JrpcClient(clientConfig);
//
//    @Benchmark
//    public void pingMessage() {
//        ClientUtil.call(client, "ping", new Object());
//    }
//
//    @Benchmark
//    public void pingMessageClient() {
//        Rsp rsp = Request.newInstance()
//                .setClient(jrpcClient)
//                .setServer("test")
//                .setMethod("ping")
//                .send();
//    }
//
//    @Benchmark
//    public void pingMessage2Client() {
//        Request.newInstance()
//                .setClient(jrpcClient)
//                .setServer("test")
//                .setMethod("ping")
//                .asyncSend()
//                .subscribe();
//    }
//
////    private Jrpc.Client client2 = ClientUtil.client(host, 5004);
////    @Benchmark
////    public void pingPns() {
////        ClientUtil.call(client2, "ping", new Object());
////    }
////
////    @Benchmark
////    public void pingPnsClient() {
////        Rsp rsp = Request.newInstance()
////                .setClient(jrpcClient)
////                .setServer("pns")
////                .setMethod("ping")
////                .send();
////    }
//
//
//    public static void main(String[] args) throws RunnerException {
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-hhmm");
//        String time = LocalDateTime.now().format(dtf);
//        Options options = new OptionsBuilder()
//                .include(PingTest.class.getSimpleName())
//                .output("./" + time + "-ping.log")
//                .build();
//        new Runner(options).run();
//    }
//}
