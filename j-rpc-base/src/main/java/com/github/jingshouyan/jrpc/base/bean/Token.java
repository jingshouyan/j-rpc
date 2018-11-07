package com.github.jingshouyan.jrpc.base.bean;

import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import com.google.common.collect.Maps;
import lombok.*;

import java.util.Map;

/**
 * @author jingshouyan
 * @date 2018/4/14 23:10
 */
@Builder@AllArgsConstructor
@NoArgsConstructor@ToString
public class Token {
    @Getter@Setter
    private String userId;
    @Getter@Setter
    private String ticket;

    private Map<String,String> headers = Maps.newHashMap();

    public boolean valid(){
        return userId != null && ticket != null;
    }


    public Token(TokenBean tokenBean){
        userId = tokenBean.getUserId();
        ticket = tokenBean.getTicket();
        if(tokenBean.getHeaders()!=null && !tokenBean.getHeaders().isEmpty()){
            headers.putAll(tokenBean.headers);
        }
    }

    public TokenBean tokenBean(){
        return new TokenBean()
                .setUserId(userId)
                .setTicket(ticket)
                .setHeaders(headers);
    }

    public Token set(String key,String value){
        if(key != null && value != null){
            headers.put(key,value);
        }
        return this;
    }

    public String get(String key){
        return headers.get(key);
    }

}
