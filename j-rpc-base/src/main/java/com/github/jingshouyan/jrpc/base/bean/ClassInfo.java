package com.github.jingshouyan.jrpc.base.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/24 17:47
 */
@Getter@Setter@ToString
public class ClassInfo {
    private String name;
    private String className;
    @JsonIgnore
    private JavaType javaType;
    private List<ClassInfo> fields = Lists.newArrayList();
    private List<ClassInfo> generics = Lists.newArrayList();
    private List<String> annotations = Lists.newArrayList();
    private int deep;
}
