package com.github.jingshouyan.jrpc.base.bean;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/27 8:59
 */
@Data
public class FieldInfo {
    private String name;
    private String type;
    private String remark;
}
