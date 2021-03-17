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

    private static final ThreadLocal<Map<String, String>> TL_HEADER = ThreadLocal.withInitial(HashMap::new);

    public static String get(String key) {
        return TL_HEADER.get().get(key);
    }

    public static void set(String key, String value) {
        if(Strings.isNullOrEmpty(key)||Strings.isNullOrEmpty(value)) {
            log.warn("key[{}] or value[{}] is empty", key, value);
        }
        TL_HEADER.get().put(key, value);
    }

    static Map<String,String> header() {
        return TL_HEADER.get();
    }

    static void header(Map<String,String> h) {
        TL_HEADER.set(h);
    }
}
