package com.github.jingshouyan.jrpc.apidoc.controller;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jingshouyan
 * #date 2018/10/26 14:58
 */
@Controller
public class DocController {

    ExecutorService exec = Executors.newFixedThreadPool(10);

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

    @RequestMapping("server/{server}")
    @ResponseBody
    public String serverInfo(@PathVariable String server){
        String str = Request.newInstance().setClient(jrpcClient)
                .setServer(server)
                .setMethod("getServerInfo")
                .send().json();
        exec.execute(()->{
            Request.newInstance().setClient(jrpcClient)
                    .setServer(server)
                    .setMethod("getServerInfo")
                    .send().json();
        });
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
