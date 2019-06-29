package com.github.jingshouyan.jrpc.base.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.util.desensitize.JsonDesensitizer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * #date 2018/10/22 17:21
 */
@ToString(exclude = {"router", "desensitizedResult"})
public class Req {
    @Getter
    @Setter
    private String method;
    @Getter
    @Setter
    private String param;

    @Getter
    @Setter
    private boolean oneway;
    @Getter
    @Setter
    private Router router;

    @JsonIgnore
    private String desensitizedParam;
    public String desensitizedParam() {
        if(param!= null){
            desensitizedParam = JsonDesensitizer.DEFAULT.desensitize(param);
        }
        return desensitizedParam;
    }

    public ReqBean reqBean() {
        return new ReqBean(method, param);
    }
}
