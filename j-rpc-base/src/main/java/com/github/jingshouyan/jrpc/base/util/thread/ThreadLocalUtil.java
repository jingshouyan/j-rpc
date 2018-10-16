package com.github.jingshouyan.jrpc.base.util.thread;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.bean.Trace;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.constant.BaseConstant;
import com.github.jingshouyan.jrpc.base.exception.JException;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author jingshouyan
 * #date 2018/10/16 17:51
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<Token> TOKEN_THREAD_LOCAL = ThreadLocal.withInitial(Token::new);
    private static final ThreadLocal<Trace> TRACE_THREAD_LOCAL = ThreadLocal.withInitial(Trace::new);

    public static void setTraceId(String traceId){
        Trace trace = new Trace();
        if(null == traceId){
            traceId = UUID.randomUUID().toString();
        }
        trace.setTraceId(traceId);
        setTrace(trace);
    }

    public static void setTrace(Trace trace){
        TRACE_THREAD_LOCAL.set(trace);
        MDC.put(BaseConstant.TRACE_ID,trace.getTraceId());
    }

    public static Trace getTrace(){
        return TRACE_THREAD_LOCAL.get();
    }

    public static void removeTrace(){
        TRACE_THREAD_LOCAL.remove();
        MDC.remove(BaseConstant.TRACE_ID);
    }

    public static void setToken(Token token){
        TOKEN_THREAD_LOCAL.set(token);
    }

    public static void removeToken(){
        TOKEN_THREAD_LOCAL.remove();
    }

    public static Token getToken(){
        return TOKEN_THREAD_LOCAL.get();
    }

    public static String userId(){
        String userId = getToken().getUserId();
        if(null == userId){
            throw new JException(Code.USERID_NOTSET);
        }
        return userId;
    }

    public static String ticket(){
        String ticket = getToken().getTicket();
        if(null == ticket){
            throw new JException(Code.TICKET_NOTSET);
        }
        return ticket;
    }
}
