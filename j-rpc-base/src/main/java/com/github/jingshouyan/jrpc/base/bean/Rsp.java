package com.github.jingshouyan.jrpc.base.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.util.desensitize.JsonMasking;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jingshouyan
 * 10/10/18 7:26 PM
 */
@ToString(exclude = {"desensitizedResult", "data"})
public class Rsp {
    @Getter
    @Setter
    private int code;
    @Getter
    @Setter
    private String message;
    @Setter
    @JsonIgnore
    private String result;

    @Getter
    @Setter
    private Object data;

    public String getResult() {
        if (result != null) {
            return result;
        }
        if (data == null) {
            return null;
        }
        result = JsonUtil.toJsonString(data);
        return result;
    }

    @JsonIgnore
    private String desensitizedResult;

    public String desensitizedResult() {
        if (desensitizedResult != null) {
            return desensitizedResult;
        }
        String result = getResult();
        if (result == null) {
            return null;
        }
        desensitizedResult = JsonMasking.DEFAULT.masking(result);
        return desensitizedResult;
    }

    public Rsp() {
    }

    public Rsp(RspBean rspBean) {
        this.code = rspBean.getCode();
        this.message = rspBean.getMessage();
        this.result = rspBean.getResult();
    }

    public boolean success() {
        return code == Code.SUCCESS;
    }

    public Rsp checkSuccess(int newCode) {
        if (!success()) {
            Object data;
            if (null != result) {
                if (newCode == code) {
                    Code.regIfAbsent(code, message);
                }
                data = JsonUtil.toBean(result, Object.class);
                if (data != null) {
                    throw new JrpcException(newCode, data);
                }
            }
            throw new JrpcException(newCode);
        }
        return this;
    }

    public Rsp checkSuccess() {
        return checkSuccess(code);
    }

    public <T> T get(Class<T> clazz) {
        return JsonUtil.toBean(result, clazz);
    }

    public Object getByType(Type type) {
        if(result == null) {
            return null;
        }
        return JsonUtil.toBean(result, type);
    }

    public <T> T get(Class<T> clazz, Class<?>... classes) {
        return JsonUtil.toBean(result, clazz, classes);
    }

    public <T> List<T> list(Class<T> clazz) {
        return JsonUtil.toList(result, clazz);
    }

    public String json() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"code\":");
        sb.append(code);
        sb.append(",\"message\":\"");
        if (null != message) {
            message = message.replace('"', '\'');
            message = message.replace('\n', ' ');
            sb.append(message);
        }
        sb.append("\"");
        String r = getResult();
        if (r != null) {
            sb.append(",\"data\":");
            sb.append(r);
        }
        sb.append("}");
        return sb.toString();
    }
}
