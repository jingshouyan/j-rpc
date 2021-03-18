package com.github.jingshouyan.jrpc.base.protocol;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * header 管理器
 *
 * @author jingshouyan
 * 2021-03-15 17:32
 **/
@Slf4j
public class HeadManager {

    private static final ThreadLocal<Map<String, String>> RECEIVE_HEADER = ThreadLocal.withInitial(HashMap::new);

    private static final ThreadLocal<Map<String, String>> REQUEST_HEADER = ThreadLocal.withInitial(HashMap::new);

    public static String getReceiveHeader(String key) {
        return RECEIVE_HEADER.get().get(key);
    }

    public static void setRequestHeader(String key, String value) {
        if(Strings.isNullOrEmpty(key)||Strings.isNullOrEmpty(value)) {
            log.warn("key[{}] or value[{}] is empty", key, value);
        }
        REQUEST_HEADER.get().put(key, value);
    }

    static void cleanReceiveHeader(){
        RECEIVE_HEADER.remove();
    }

    static void cleanRequestHeader() {
        RECEIVE_HEADER.remove();
    }



    static Map<String,String> requestHeader() {
        return REQUEST_HEADER.get();
    }

    static void receiveHeader(Map<String,String> h) {
        RECEIVE_HEADER.set(h);
    }
}
