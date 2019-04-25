package com.github.jingshouyan.jrpc.base.bean;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/27 8:56
 */
@Data
public class TypeInfo {
    private String type;
    private String remark;
    private List<FieldInfo> fields = Lists.newArrayList();
    private List<String> annotations = Lists.newArrayList();
}
