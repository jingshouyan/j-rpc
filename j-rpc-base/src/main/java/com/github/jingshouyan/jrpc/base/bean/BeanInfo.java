package com.github.jingshouyan.jrpc.base.bean;

import lombok.Data;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/27 9:05
 */
@Data
public class BeanInfo {
    private String rootType;
    private List<TypeInfo> types;
}
