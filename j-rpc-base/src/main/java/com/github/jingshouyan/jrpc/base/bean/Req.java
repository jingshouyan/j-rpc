package com.github.jingshouyan.jrpc.base.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jingshouyan.jrpc.base.thrift.ReqBean;
import com.github.jingshouyan.jrpc.base.util.desensitize.JsonMasking;
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
        if (desensitizedParam == null && param != null) {
            desensitizedParam = JsonMasking.DEFAULT.masking(param);
        }
        return desensitizedParam;
    }

    public ReqBean reqBean() {
        return new ReqBean(method, param);
    }
}
