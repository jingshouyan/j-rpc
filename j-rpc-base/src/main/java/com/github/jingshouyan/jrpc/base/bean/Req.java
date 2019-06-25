package com.github.jingshouyan.jrpc.base.bean;

import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * #date 2018/10/22 17:21
 */
@Getter
@Setter
@ToString(exclude = {"router"})
public class Req {

    private String method;
    private String param;
    private boolean oneway;

    private Router router;

    public ReqBean reqBean() {
        return new ReqBean(method, param);
    }
}
