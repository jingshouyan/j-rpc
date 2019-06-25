package com.github.jingshouyan.jrpc.server.method;

import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.constant.BaseConstant;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
public interface BaseMethod<T, R> {

    Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    int MAX_ERROR = 6;

    /**
     * 子类调用时获取接口中的入参泛型
     *
     * @return 泛型
     */
    default Type getInputType() {
        return getType(0);
    }

    /**
     * 子类调用时获取接口中的返回值泛型
     *
     * @return 泛型
     */
    default Type getOutputType() {
        return getType(1);
    }

    /**
     * 获取实现类指定的泛型
     *
     * @param index 序号
     * @return 泛型
     */
    default Type getType(int index) {
        Type type = null;
        Type[] ts = getClass().getGenericInterfaces();
        for (int i = 0; i < ts.length; i++) {
            if (ts[i] instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) ts[i];
                Class c = (Class) t.getRawType();
                if (BaseMethod.class.isAssignableFrom(c)) {
                    type = t.getActualTypeArguments()[index];
                }
            }
        }
        return type;
    }

    /**
     * 校验请求参数
     *
     * @param t 参数
     */
    default void validate(T t) {
        Set<ConstraintViolation<T>> cvs = VALIDATOR.validate(t);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (ConstraintViolation<T> cv : cvs) {
            if (i >= MAX_ERROR) {
                break;
            }
            String message = cv.getMessage();
            if (message.startsWith(BaseConstant.INVALID_CODE_PREFIX)) {
                int code = Integer.parseInt(message.substring(BaseConstant.INVALID_CODE_PREFIX.length()));
                throw new JrpcException(code);
            }
            sb.append(cv.getPropertyPath().toString());
            sb.append(" ");
            sb.append(message);
            sb.append("\t");
            i++;
        }
        if (!cvs.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
            throw new JrpcException(Code.PARAM_INVALID, null, sb.toString());
        }
    }

}
