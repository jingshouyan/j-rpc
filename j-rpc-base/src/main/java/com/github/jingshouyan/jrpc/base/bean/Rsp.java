package com.github.jingshouyan.jrpc.base.bean;

import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.base.thrift.RspBean;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jingshouyan
 * 10/10/18 7:26 PM
 */
@Getter@Setter@ToString
public class Rsp {
    private int code;
    private String message;
    private String result;
    private Object data;

    public String getResult(){
        if(result == null && data == null){
            return null;
        }
        if(result == null && data != null){
            result = JsonUtil.toJsonString(data);
        }
        return result;
    }

    public Rsp(){}

    public Rsp(RspBean rspBean) {
        this.code = rspBean.getCode();
        this.message = rspBean.getMessage();
        this.result = rspBean.getResult();
    }

    public boolean success(){
        return code == Code.SUCCESS;
    }

    public Rsp checkSuccess(int newCode) {
        if (!success()){
            Object data;
            if(null != result){
                if(newCode == code) {
                    Code.regIfAbsent(code,message);
                }
                data = JsonUtil.toBean(result,Object.class);
                if(data != null){
                    throw new JrpcException(newCode,data);
                }
            }
            throw new JrpcException(newCode);
        }
        return this;
    }

    public Rsp checkSuccess() {
        return checkSuccess(code);
    }

    public <T> T get(Class<T> clazz){
        return JsonUtil.toBean(result,clazz);
    }

    public <T> T get(Class<T> clazz,Class<?>... classes){
        return JsonUtil.toBean(result,clazz,classes);
    }

    public <T> List<T> list(Class<T> clazz){
        return JsonUtil.toList(result,clazz);
    }

    public String json(){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"code\":");
        sb.append(code);
        sb.append(",\"message\":\"");
        if(null != message) {
            message = message.replace('"','\'');
            message = message.replace('\n',' ');
            sb.append(message);
        }
        sb.append("\"");
        if (result == null && data != null) {
            result = JsonUtil.toJsonString(data);
        }
        if(result != null){
            sb.append(",\"data\":");
            sb.append(result);
        }
        sb.append("}");
        return sb.toString();
    }
}
