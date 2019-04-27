package com.github.jingshouyan.jrpc.base.bean;

import lombok.*;

/**
 * @author jingshouyan
 * #date 2018/10/23 11:35
 */
@Getter
@Setter
@ToString
@NoArgsConstructor@AllArgsConstructor
public class CodeInfo {
    private int code;
    private String message;
    private String whoUse;
}
