package com.github.jingshouyan.jrpc.server;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jingshouyan
 * #date 2018/10/23 20:40
 */
@Data
public class TestBean2<T,R,X> {
    private String test;
    private List<T> data;
    private List<Map<String,R>> mapList;
    private Set<X> set;
}
