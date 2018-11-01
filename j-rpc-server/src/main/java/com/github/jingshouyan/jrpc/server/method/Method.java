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
public interface Method<T,R> {

    Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 子类调用时获取接口中的第一个泛型
     * @return 泛型
     */
    @SuppressWarnings("unchecked")
    default Type getInputType() {
        return getType(0);
    }

    default Type getOutputType() {
        return getType(1);
    }

    default Type getType(int index){
        Type type = null;
        Type[] ts = getClass().getGenericInterfaces();
        for (int i = 0; i < ts.length; i++) {
            if(ts[i] instanceof ParameterizedType){
                ParameterizedType t = (ParameterizedType)ts[i];
                if (t.getRawType() == Method.class){
                    type = t.getActualTypeArguments()[index];
                }
            }
        }
        return type;
    }

    /**
     * 校验请求参数
     * @param t 参数
     */
    default void validate(T t){
        Set<ConstraintViolation<T>> cvs = VALIDATOR.validate(t);
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<T> cv : cvs) {
            String message = cv.getMessage();
            if(message.startsWith(BaseConstant.INVALID_CODE_PREFIX)){
                int code = Integer.parseInt(message.substring(BaseConstant.INVALID_CODE_PREFIX.length()));
                throw new JException(code);
            }
            sb.append(cv.getPropertyPath().toString());
            sb.append(" ");
            sb.append(message);
            sb.append("\t");
        }
        if(!cvs.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
            throw new JException(Code.PARAM_INVALID,sb.toString());
        }
    }

    /**
     * 执行业务
     * @param token 用户信息
     * @param t 入参
     * @return 执行结果
     */
    default R validAndAction(Token token, T t){
        validate(t);
        return action(token, t);
    }

    /**
     * 执行业务
     * @param token 用户信息
     * @param t 入参
     * @return 执行结果
     */
    R action(Token token, T t);
}
