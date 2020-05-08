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

    public static final int BIT_STRING_END = 0;
    public static final int BIT_STRING_START = 1;

    public static final char CHAR_MASK = '*';
    public static final char CHAR_NULL = '\0';


    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    private final Map<String, Integer> SETTINGS = Maps.newConcurrentMap();

    public static final JsonMasking DEFAULT = new JsonMasking();

    /**
     * 添加脱敏配置
     *
     * @param key     需要脱敏的key
     * @param setting 10进制,当值字符串时:第0位表示原文显示后缀个数,第1位表示原文显示前缀个数
     */
    public void addSetting(String key, int setting) {
        SETTINGS.put(key, setting);
    }

    /**
     * 添加脱敏配置
     *
     * @param settings 脱敏配置
     *                 key 需要脱敏的key
     *                 setting 10进制,当值字符串时:第0位表示原文显示后缀个数,第1位表示原文显示前缀个数
     */
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
        if (json == null || SETTINGS.isEmpty()) {
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
            StringBuilder sb = new StringBuilder();
            for (char c : chars) {
                if(c != CHAR_NULL) {
                    sb.append(c);
                }
            }
            return sb.toString();
        } catch (Throwable e) {
            log.warn("json desensitize error.", e);
            return json;
        }
    }

    private void stringMasking(char[] chars, JsonParser parser, int setting) throws IOException {
        // 头部保留长度
        int prefixLen = getDigit(setting, BIT_STRING_START);
        // 尾部保留长度
        int suffixLen = getDigit(setting, BIT_STRING_END);
        String value = parser.getValueAsString();
        int valueLen = value.length();
        if (prefixLen + suffixLen < valueLen) {
            int offset = parser.getTextOffset();
            int start = offset + prefixLen;
            int end = offset + valueLen - suffixLen;
            for (int i = start; i < end; i++) {
                if(i == start) {
                    chars[i] = CHAR_MASK;
                } else {
                    chars[i] = CHAR_NULL;
                }

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
                chars[i] = CHAR_NULL;
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
            chars[i] = CHAR_NULL;
        }
    }

    private int getDigit(int source, int n) {
        int x = (int) Math.pow(10, n);
        return source / x % 10;
    }
}
