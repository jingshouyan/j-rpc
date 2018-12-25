package com.jing.test.constant;

import com.github.jingshouyan.jrpc.base.code.Code;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2018/12/25 19:46
 */
@Component
public class TestCode {
    public static final int NAME_IS_NULL = 200;
    public static final int JUST_ERROR = 201;
    public static final int JUST_ERROR_WITH_DATA = 202;
    static {
        Code.regCode(NAME_IS_NULL,"name is null");
        Code.regCode(JUST_ERROR,"就是想返回个错误");
        Code.regCode(JUST_ERROR,"就是想返回个错误并且带点数据");
    }
}
