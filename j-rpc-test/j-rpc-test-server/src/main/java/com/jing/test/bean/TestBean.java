package com.jing.test.bean;

import lombok.Data;

/**
 * @author jingshouyan
 * #date 2018/10/27 11:11
 */
@Data
public class TestBean<T> {
    private T t;
}
