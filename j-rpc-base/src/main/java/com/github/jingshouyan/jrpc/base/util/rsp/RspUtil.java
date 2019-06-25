package com.github.jingshouyan.jrpc.base.util.rsp;

import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;

/**
 * @author jingshouyan
 * #date 2018/10/22 23:33
 */
public class RspUtil {

    public static Rsp success() {
        return success(null);
    }

    public static Rsp success(Object result) {
        return error(Code.SUCCESS, result);
    }

    public static Rsp error(int code) {
        return error(code, Code.getMessage(code), null);
    }

    /**
     * 生成Rsp对象
     * @param code   错误码
     * @param result 返回对象
     * @return Rsp对象  msg根据code对应的消息 result json序列化
     */
    public static Rsp error(int code, Object result) {
        return error(code, Code.getMessage(code), result);
    }

    public static Rsp error(JrpcException e) {
        int code = e.getCode();
        String message = Code.getMessage(code);
        String detail = e.getDetail();
        if (detail != null) {
            message += ":" + detail;
        }
        Object data = e.getData();
        return error(code, message, data);
    }


    private static Rsp error(int code, String message, Object data) {
        Rsp res = new Rsp();
        res.setCode(code);
        res.setData(data);
        res.setMessage(message);
        return res;
    }


}
