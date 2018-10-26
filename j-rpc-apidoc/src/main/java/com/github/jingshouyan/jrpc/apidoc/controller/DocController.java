package com.github.jingshouyan.jrpc.apidoc.controller;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.client.RequestBuilder;
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
    public Map<String,List<ServerInfo>> serverMap(){
        return requestBuilder.serverMap();
    }

    @RequestMapping("server/{server}/{instance}")
    @ResponseBody
    public String serverInfo(@PathVariable String server,@PathVariable String instance){
        return requestBuilder.newRequest()
                .setServer(server).setInstance(instance)
                .setMethod("getServerInfo")
                .send().json();
    }

    @PostConstruct
    public void init(){
        IntStream.rangeClosed(0,1000).parallel()
                .forEach(i -> {
                    requestBuilder.newRequest()
                            .setServer("test")
                            .setMethod("getServerInfo")
                            .send().json();
                });
    }
}
