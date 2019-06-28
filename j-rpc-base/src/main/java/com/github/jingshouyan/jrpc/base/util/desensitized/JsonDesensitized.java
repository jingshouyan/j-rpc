package com.github.jingshouyan.jrpc.base.util.desensitized;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2019/6/28 15:46
 */
@Slf4j
public class JsonDesensitized {

    private static final JsonFactory factory = new JsonFactory();
    private static final int SPLIT_INT = 100;

    /**
     * json 字符串 脱敏
     * @param json json 字符串
     * @param conf 脱敏配置
     * @return 脱敏后数据
     */
    public static String desensitized(String json, Map<String, Integer> conf) {
        try {
            char[] chars = json.toCharArray();
            JsonParser parser = factory.createParser(chars);
            do {
                JsonToken token = parser.nextToken();
                if (token == JsonToken.FIELD_NAME) {
                    String key = parser.currentName();
                    token = parser.nextToken();
                    if (token == JsonToken.VALUE_STRING) {
                        Integer intConf = conf.get(key);
                        if (null != intConf) {
                            // 头部保留长度
                            int prefixLen = intConf / SPLIT_INT;
                            // 尾部保留长度
                            int suffixLen = intConf % SPLIT_INT;
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
            log.warn("json desensitized error.", e);
            return json;
        }
    }
}
