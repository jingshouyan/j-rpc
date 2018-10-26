package com.github.jingshouyan.jrpc.apidoc.controller;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.client.RequestBuilder;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author jingshouyan
 * #date 2018/10/26 14:58
 */
@Controller
public class DocController {

    @Resource
    private RequestBuilder requestBuilder;

    @RequestMapping("servers")
    @ResponseBody
    public Rsp serverMap(){
        Map<String,List<ServerInfo>> map = requestBuilder.serverMap();
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
        return requestBuilder.newRequest()
                .setServer(server)
                .setMethod("getServerInfo")
                .send().json();
    }

//    @PostConstruct
//    public void init(){
//        IntStream.rangeClosed(0,1000).parallel()
//                .forEach(i -> {
//                    requestBuilder.newRequest()
//                            .setServer("test")
//                            .setMethod("getServerInfo")
//                            .send().json();
//                });
//    }
}
