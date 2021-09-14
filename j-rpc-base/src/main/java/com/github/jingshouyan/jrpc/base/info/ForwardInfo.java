package com.github.jingshouyan.jrpc.base.info;

import lombok.Data;

/**
 * 转发信息
 *
 * @author jingshouyan
 * 2021-09-14 19:38
 **/
@Data
public class ForwardInfo {
    private String origin;

    private String service;
    private String version;
    private String method;
}
