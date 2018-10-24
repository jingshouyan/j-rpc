package com.github.jingshouyan.jrpc.base.bean;

import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jingshouyan
 * @date 2018/4/14 23:10
 */
@Data@Builder@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private String userId;
    private String ticket;
    private String ext;

    public boolean valid(){
        return userId != null && ticket != null;
    }


    public Token(TokenBean tokenBean){
        userId = tokenBean.getUserId();
        ticket = tokenBean.getTicket();
        ext = tokenBean.getExt();
    }

    public TokenBean tokenBean(){
        return new TokenBean()
                .setUserId(userId)
                .setTicket(ticket)
                .setExt(ext);
    }

    public Token copy(){
        Token token = new Token();
        token.setUserId(userId);
        token.setTicket(ticket);
        token.setExt(ext);
        return token;
    }
}
