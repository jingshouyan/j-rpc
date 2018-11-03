package com.github.jingshouyan.jrpc.apidoc.controller;

import brave.Tracing;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * @author jingshouyan
 * #date 2018/10/26 14:58
 */
@Controller
@Slf4j
public class DocController {



    ExecutorService exec;

    public DocController(Tracing tracing){
        ExecutorService exec = Executors.newFixedThreadPool(10);
        this.exec = tracing.currentTraceContext().executorService(exec);
        this.exec = exec;
    }

    @Resource
    private JrpcClient jrpcClient;

    @RequestMapping("servers")
    @ResponseBody
    public Rsp serverMap(){

        Map<String,List<ServerInfo>> map = jrpcClient.serverMap();
        List<ServerInfo> serverInfos = Lists.newArrayList();
        for(List<ServerInfo> l: map.values()){
            if(l != null&& !l.isEmpty()){
                serverInfos.add(l.get(0));
            }
        }
        return RspUtil.success(serverInfos);
    }
    private static final TransmittableThreadLocal<String> TL_TEST = new TransmittableThreadLocal<>();
    @RequestMapping("server/{server}")
    @ResponseBody
    public String serverInfo(@PathVariable String server){

        String str = Request.newInstance().setClient(jrpcClient)
                .setServer(server)
                .setMethod("getServerInfo")
                .send().json();

        TL_TEST.set("abc"+System.currentTimeMillis());
        log.info("M:TL_TEST:{}",TL_TEST.get());
//        for (int i = 0; i < 10; i++) {
//            exec.execute(new Runnable() {
//                @Override
//                public void run() {
//                    log.info("M:TL_TEST:{}",TL_TEST.get());
//                }
//            });
//        }
        IntStream.range(0,10)
                .parallel()
                .forEach(i ->{
//            exec.execute(()->{
                log.info("R:TL_TEST:{}",TL_TEST.get());
                Request.newInstance().setClient(jrpcClient)
                        .setServer(server)
                        .setMethod("getServerInfo")
                        .send().json();
//            });
        });

        List<String> list = new ArrayList<>();
        list.parallelStream().mapToInt(String::length).sum();

        return str;
    }

//    @PostConstruct
//    public void init(){
//        IntStream.rangeClosed(0,1000).parallel()
//                .forEach(i -> {
//                    jrpcClient.newRequest()
//                            .setServer("test")
//                            .setMethod("getServerInfo")
//                            .send().json();
//                });
//    }
}
