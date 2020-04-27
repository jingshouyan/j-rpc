package com.github.jingshouyan.jrpc.base.code;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jingshouyan
 * #date 2018/10/16 17:04
 */
public class Code {
    public static final int SUCCESS = 1;
    public static final int CLIENT_ERROR = -1;
    public static final int GET_SERVER_ADDRESS_TIMEOUT = -2;
    public static final int CONNECT_TIMEOUT = -3;
    public static final int SERVER_NOT_FOUND = -101;
    public static final int INSTANCE_NOT_FUND = -102;
    public static final int VERSION_SERVER_NOT_FUND = -103;
    public static final int UNSUPPORTED_ROUTE_MODE = -2;
    public static final int SERVER_ERROR = -201;
    public static final int METHOD_NOT_FOUND = -202;
    public static final int JSON_PARSE_ERROR = -203;
    public static final int PARAM_INVALID = -204;
    public static final int USERID_NOTSET = -205;
    public static final int TICKET_NOTSET = -206;
    public static final int BAD_REQUEST = -207;
    public static final int PERMISSION_DENIED = -208;
    private static final Map<Integer, String> CODE_MAP = new ConcurrentHashMap<>();

    static {
        CODE_MAP.put(SUCCESS, "success");
        CODE_MAP.put(CLIENT_ERROR, "client error");
        CODE_MAP.put(GET_SERVER_ADDRESS_TIMEOUT, "get server address timeout");
        CODE_MAP.put(CONNECT_TIMEOUT, "connect timeout");
        CODE_MAP.put(SERVER_NOT_FOUND, "server not found");
        CODE_MAP.put(INSTANCE_NOT_FUND, "instance not found");
        CODE_MAP.put(VERSION_SERVER_NOT_FUND, "version server not found");
        CODE_MAP.put(UNSUPPORTED_ROUTE_MODE, "unsupported route mode");
        CODE_MAP.put(SERVER_ERROR, "server error");
        CODE_MAP.put(METHOD_NOT_FOUND, "method not found");
        CODE_MAP.put(JSON_PARSE_ERROR, "json parse error");
        CODE_MAP.put(PARAM_INVALID, "param invalid");
        CODE_MAP.put(USERID_NOTSET, "userId not set");
        CODE_MAP.put(TICKET_NOTSET, "ticket not set");
        CODE_MAP.put(BAD_REQUEST, "bad request");
        CODE_MAP.put(PERMISSION_DENIED, "permission denied");
    }

    /**
     * 获取错误信息
     *
     * @param code 错误码
     * @return 错误信息
     */
    public static String getMessage(int code) {
        String message = CODE_MAP.get(code);
        if (null == message) {
            message = "code[" + code + "] is undefined";
        }
        return message;
    }

    /**
     * 注册错误码
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public static void regCode(int code, String message) {
        String msg = CODE_MAP.get(code);
        if (null != msg) {
            System.err.println("code :" + code + " already in use. old message:[" + msg + "]");
        }
        CODE_MAP.put(code, message);
    }

    public static void regIfAbsent(int code, String message) {
        CODE_MAP.putIfAbsent(code, message);
    }

    public static Map<Integer, String> getCodeMap() {
        return Maps.newHashMap(CODE_MAP);
    }
}