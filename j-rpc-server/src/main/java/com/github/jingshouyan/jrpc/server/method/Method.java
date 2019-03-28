package com.github.jingshouyan.jrpc.server.method;


import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.constant.BaseConstant;
import com.github.jingshouyan.jrpc.base.exception.JException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author jingshouyan
 * #date 2018/10/22 16:10
 */
public interface Method<T,R> extends BaseMethod<T,R>{


    /**
     * 执行业务
     * @param token 用户信息
     * @param t 入参
     * @return 执行结果
     */
    R action(Token token, T t);
}
