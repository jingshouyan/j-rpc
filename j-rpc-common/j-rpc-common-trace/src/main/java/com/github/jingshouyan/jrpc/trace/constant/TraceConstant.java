package com.github.jingshouyan.jrpc.trace.constant;

/**
 * @author jingshouyan
 * #date 2018/11/2 20:01
 */
public interface TraceConstant {
    String HEADER_TRACE = "X-B3-SIMPLE";
    String CS = "cs";
    String SR = "sr";
    String SS = "ss";
    String CR = "cr";
    String TAG_METHOD = "i.method";
    String TAG_USER_ID = "i.userId";
    String TAG_TICKET = "i.ticket";
    String TAG_PARAM = "i.param";
    String TAG_CODE = "o.code";
    String TAG_MESSAGE = "o.message";
    String TAG_DATA = "o.data";
    String TAG_ERROR = "error";
    String TAG_ARG_PREFIX = "arg.";
    String TAG_RESULT = "result";
    String CALL_PATH = "call.path";
}
