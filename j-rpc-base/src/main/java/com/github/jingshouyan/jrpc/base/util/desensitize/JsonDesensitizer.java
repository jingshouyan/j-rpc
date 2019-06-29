package com.github.jingshouyan.jrpc.base.util.desensitize;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2019/6/28 15:46
 */
@Slf4j
public class JsonDesensitizer {

    private static final JsonFactory factory = new JsonFactory();
    private static final int SPLIT_INT = 100;
    private final Map<String,Integer> SETTINGS = Maps.newConcurrentMap();

    public static final JsonDesensitizer DEFAULT = new JsonDesensitizer();

    public void addConf(String key,int setting) {
        SETTINGS.put(key,setting);
    }

    public void addConf(Map<String,Integer> settings) {
        SETTINGS.putAll(settings);
    }
    /**
     * json 字符串 脱敏
     * @param json json 字符串
     * @return 脱敏后数据
     */
    public String desensitize(String json) {
        if(SETTINGS.isEmpty()){
            return json;
        }
        try {
            char[] chars = json.toCharArray();
            JsonParser parser = factory.createParser(chars);
            do {
                JsonToken token = parser.nextToken();
                if (token == JsonToken.FIELD_NAME) {
                    String key = parser.currentName();
                    token = parser.nextToken();
                    if (token == JsonToken.VALUE_STRING) {
                        Integer setting = SETTINGS.get(key);
                        if (null != setting) {
                            // 头部保留长度
                            int prefixLen = setting / SPLIT_INT;
                            // 尾部保留长度
                            int suffixLen = setting % SPLIT_INT;
                            String value = parser.getValueAsString();
                            int valueLen = value.length();
                            if (prefixLen + suffixLen < valueLen) {
                                int offset = parser.getTextOffset();
                                int start = offset + prefixLen;
                                int end = offset + valueLen - suffixLen;
                                for (int i = start; i < end; i++) {
                                    chars[i] = '*';
                                }
                            }
                        }
                    }
                }
            } while (parser.hasCurrentToken());
            return new String(chars);
        } catch (Throwable e) {
            log.warn("json desensitize error.", e);
            return json;
        }
    }
}
