package com.github.jingshouyan.jrpc.base.util.desensitize;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * @author jingshouyan
 * #date 2019/6/28 15:46
 */
@Slf4j
public class JsonMasking {

    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    private static final int SPLIT_INT = 100;
    private final Map<String, Integer> SETTINGS = Maps.newConcurrentMap();

    public static final JsonMasking DEFAULT = new JsonMasking();

    public void addSetting(String key, int setting) {
        SETTINGS.put(key, setting);
    }

    public void addSetting(Map<String, Integer> settings) {
        SETTINGS.putAll(settings);
    }

    /**
     * json 字符串 脱敏
     *
     * @param json json 字符串
     * @return 脱敏后数据
     */
    public String masking(String json) {
        if (SETTINGS.isEmpty()) {
            return json;
        }
        try {
            char[] chars = json.toCharArray();
            JsonParser parser = JSON_FACTORY.createParser(chars);
            do {
                JsonToken token = parser.nextToken();
                if (token == JsonToken.FIELD_NAME) {
                    String key = parser.currentName();
                    token = parser.nextToken();
                    Integer setting = SETTINGS.get(key);
                    if (null != setting) {
                        switch (token) {
                            case VALUE_STRING:
                                stringMasking(chars, parser, setting);
                                break;
                            case VALUE_NUMBER_INT:
                            case VALUE_NUMBER_FLOAT:
                                numberMasking(chars, parser, setting);
                                break;
                            case START_OBJECT:
                            case START_ARRAY:
                                objectMasking(chars, parser, setting);
                            default:
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

    private void stringMasking(char[] chars, JsonParser parser, int setting) throws IOException {
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

    private void numberMasking(char[] chars, JsonParser parser, int setting) throws IOException {
        int start = parser.getTextOffset();
        String value = parser.getValueAsString();
        int valueLen = value.length();
        int end = start + valueLen;
        for (int i = start; i < end; i++) {
            if (i == start) {
                chars[i] = '0';
            } else {
                chars[i] = ' ';
            }
        }
    }

    private void objectMasking(char[] chars, JsonParser parser, int setting) throws IOException {

        int flag = 1;
        int start = (int) parser.getCurrentLocation().getCharOffset();
        while (flag > 0) {
            JsonToken token = parser.nextToken();
            if (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY) {
                flag++;
            }
            if (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY) {
                flag--;
            }
        }
        int end = (int) parser.getCurrentLocation().getCharOffset();
        for (int i = start; i < end - 1; i++) {
            chars[i] = ' ';
        }
    }
}
