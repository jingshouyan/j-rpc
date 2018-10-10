package com.github.jingshouyan.jrpc.base.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * 10/10/18 7:26 PM
 */
@Getter@Setter@ToString
public class Rsp {
    private int code;
    private String message;
    private String result;
}
