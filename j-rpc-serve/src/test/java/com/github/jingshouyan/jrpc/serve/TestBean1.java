package com.github.jingshouyan.jrpc.serve;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jingshouyan.jrpc.serve.bean.CodeInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
