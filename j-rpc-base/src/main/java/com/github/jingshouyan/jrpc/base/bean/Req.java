package com.github.jingshouyan.jrpc.base.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * #date 2018/10/22 17:21
 */
@Getter
@Setter
@ToString
public class Req {

    private String method;
    private String param;
    private boolean oneway;
}
