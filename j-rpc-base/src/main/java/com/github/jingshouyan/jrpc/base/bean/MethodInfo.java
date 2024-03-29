package com.github.jingshouyan.jrpc.base.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * #date 2018/10/23 11:16
 */
@Getter
@Setter
@ToString
public class MethodInfo {
    String name;
    String remark = "";
    BeanInfo input;
    BeanInfo output;
}
