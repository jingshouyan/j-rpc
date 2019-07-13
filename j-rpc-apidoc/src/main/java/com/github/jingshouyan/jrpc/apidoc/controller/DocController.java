package com.github.jingshouyan.jrpc.apidoc.controller;

import com.github.jingshouyan.jrpc.base.bean.CodeInfo;
import com.github.jingshouyan.jrpc.base.bean.InterfaceInfo;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.util.rsp.RspUtil;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * #date 2018/10/26 14:58
 */
@RestController
@Slf4j
@RequestMapping("api")
public class DocController {


    @Resource
    private JrpcClient jrpcClient;

    @RequestMapping("servers")
    public String servers() {

        Map<String, List<ServerInfo>> map = jrpcClient.serverMap();
        List<ServerInfo> serverInfos = Lists.newArrayList();
        for (List<ServerInfo> l : map.values()) {
            if (l != null && !l.isEmpty()) {
                serverInfos.add(l.get(0));
            }
        }
        return RspUtil.success(serverInfos).json();
    }

    @RequestMapping("codes")
    public String codes() {
        Map<String, List<ServerInfo>> map = jrpcClient.serverMap();
        List<String> servers = map.values().stream().filter(Objects::nonNull)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).getName())
                .collect(Collectors.toList());
        List<CodeInfo> codes = Lists.newArrayList();
        for (String server : servers) {
            Rsp rsp = Request.newInstance().setClient(jrpcClient)
                    .setServer(server)
                    .setMethod("getNode")
                    .send();
            if (rsp.success()) {
                InterfaceInfo info = rsp.get(InterfaceInfo.class);
                List<CodeInfo> codeInfos = info.getCodeInfos();
                codeInfos.forEach(c -> c.setWhoUse(server));
                codes.addAll(codeInfos);
            }
        }
        Map<String, CodeInfo> cmap = Maps.newHashMap();
        for (CodeInfo code : codes) {
            String key = code.getCode() + ":" + code.getMessage();
            if (cmap.containsKey(key)) {
                CodeInfo code2 = cmap.get(key);
                code2.setWhoUse(code2.getWhoUse() + "," + code.getWhoUse());
            } else {
                cmap.put(key, code);
            }
        }
        codes = Lists.newArrayList(cmap.values());
        codes.sort(Comparator.comparingInt(CodeInfo::getCode));
        return RspUtil.success(codes).json();
    }

    @RequestMapping("server/{server}")
    public String serverInfo(@PathVariable String server) {

        String str = Request.newInstance().setClient(jrpcClient)
                .setServer(server)
                .setMethod("getNode")
                .send().json();


        return str;
    }


}
