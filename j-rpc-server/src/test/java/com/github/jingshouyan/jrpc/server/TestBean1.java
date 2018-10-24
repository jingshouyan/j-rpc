package com.github.jingshouyan.jrpc.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jingshouyan.jrpc.server.bean.CodeInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jingshouyan
 * #date 2018/10/23 23:42
 */
@Data
public class TestBean1 {
    @JsonIgnore@NotNull
    private TestBean2<TestBean3,String,CodeInfo> testBean2;
}
