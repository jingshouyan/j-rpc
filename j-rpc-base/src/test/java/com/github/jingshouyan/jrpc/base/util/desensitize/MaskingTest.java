package com.github.jingshouyan.jrpc.base.util.desensitize;

import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2019/6/28 17:05
 */

public class MaskingTest {
    private static final String json = "[{\"createdAt\":-1547201744645645645645676.92e2,\"updatedAt\":1547201747692,\"deletedAt\":-1,\"id\":\"U10001@abc\",\"nickname\":\"靖守彦\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805020\",\"type\":1}],\"version\":10001,\"tinyVersion\":10001},{\"createdAt\":1547201748198,\"updatedAt\":1547201748198,\"deletedAt\":-1,\"id\":\"U10002@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805021\",\"type\":1}],\"version\":10002,\"tinyVersion\":10002},{\"createdAt\":1547201748628,\"updatedAt\":1547201748628,\"deletedAt\":-1,\"id\":\"U10003@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805022\",\"type\":1}],\"version\":10003,\"tinyVersion\":10003}," +
            "\r\n" +
            "{\"createdAt\":1547201749058,\"updatedAt\":1547201749058,\"deletedAt\":-1,\"id\":\"U10004@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805023\",\"type\":1}],\"version\":10004,\"tinyVersion\":10004},{\"createdAt\":1547201749749,\"updatedAt\":1547201749749,\"deletedAt\":-1,\"id\":\"U10005@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805024\",\"type\":1}],\"version\":10005,\"tinyVersion\":10005},{\"createdAt\":1547201750218,\"updatedAt\":1547201750218,\"deletedAt\":-1,\"id\":\"U10006@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805025\",\"type\":1}],\"version\":10006,\"tinyVersion\":10006},{\"createdAt\":1547201750689,\"updatedAt\":1547201750689,\"deletedAt\":-1,\"id\":\"U10007@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805026\",\"type\":1}],\"version\":10007,\"tinyVersion\":10007},{\"createdAt\":1547201751140,\"updatedAt\":1547201751140,\"deletedAt\":-1,\"id\":\"U10008@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805027\",\"type\":1}],\"version\":10008,\"tinyVersion\":10008},{\"createdAt\":1547201751666,\"updatedAt\":1547201751666,\"deletedAt\":-1,\"id\":\"U10009@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805028\",\"type\":1}],\"version\":10009,\"tinyVersion\":10009},{\"createdAt\":1547201752100,\"updatedAt\":1547208051633,\"deletedAt\":-1,\"id\":\"U10010@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"createdAt\":1547208051504,\"updatedAt\":1547208051504,\"deletedAt\":-1,\"account\":\"abc@123.com\",\"type\":2,\"userId\":\"U10010@abc\"}],\"version\":24097,\"tinyVersion\":10010}]";

    private static final String json2 = "[{\"createdAt\":-15472017476.92e2,\"updatedAt\":1547201747692,\"deletedAt\":-1,\"id\":\"U10001@abc\",\"nickname\":\"靖守彦\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805020\",\"type\":[{\"a\":[{}]}]}],\"version\":10001,\"tinyVersion\":10001},{\"createdAt\":1547201748198,\"updatedAt\":1547201748198,\"deletedAt\":-1,\"id\":\"U10002@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805021\",\"type\":1}],\"version\":10002,\"tinyVersion\":10002},{\"createdAt\":1547201748628,\"updatedAt\":1547201748628,\"deletedAt\":-1,\"id\":\"U10003@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805022\",\"type\":1}],\"version\":10003,\"tinyVersion\":10003},\n" +
            "{\"createdAt\":1547201749058,\"updatedAt\":1547201749058,\"deletedAt\":-1,\"id\":\"U10004@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805023\",\"type\":1}],\"version\":10004,\"tinyVersion\":10004},{\"createdAt\":1547201749749,\"updatedAt\":1547201749749,\"deletedAt\":-1,\"id\":\"U10005@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805024\",\"type\":1}],\"version\":10005,\"tinyVersion\":10005},{\"createdAt\":1547201750218,\"updatedAt\":1547201750218,\"deletedAt\":-1,\"id\":\"U10006@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805025\",\"type\":1}],\"version\":10006,\"tinyVersion\":10006},{\"createdAt\":1547201750689,\"updatedAt\":1547201750689,\"deletedAt\":-1,\"id\":\"U10007@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805026\",\"type\":1}],\"version\":10007,\"tinyVersion\":10007},{\"createdAt\":1547201751140,\"updatedAt\":1547201751140,\"deletedAt\":-1,\"id\":\"U10008@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805027\",\"type\":1}],\"version\":10008,\"tinyVersion\":10008},{\"createdAt\":1547201751666,\"updatedAt\":1547201751666,\"deletedAt\":-1,\"id\":\"U10009@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"account\":\"0086-18552805028\",\"type\":1}],\"version\":10009,\"tinyVersion\":10009},{\"createdAt\":1547201752100,\"updatedAt\":1547208051633,\"deletedAt\":-1,\"id\":\"U10010@abc\",\"nickname\":\"jingle\",\"avatar\":\"\",\"id\":\"abc\",\"gender\":1,\"label\":{},\"accounts\":[{\"createdAt\":1547208051504,\"updatedAt\":1547208051504,\"deletedAt\":-1,\"account\":\"abc@123.com\",\"type\":2,\"userId\":\"U10010@abc\"}],\"version\":24097,\"tinyVersion\":10010}]\n";

    public static void main(String[] args) {
        Map<String, Integer> conf = Maps.newHashMap();
        conf.put("nickname", 10);
        conf.put("account", 73);
//        conf.put("accounts", 0);
        conf.put("createdAt", 0);
        conf.put("label", 0);
        JsonMasking.DEFAULT.addSetting(conf);
        String masked = JsonMasking.DEFAULT.masking(json);
        System.out.println(masked);
        System.out.println(json);
        String masked2 = JsonMasking.DEFAULT.masking(json2);
        System.out.println(masked2);
        System.out.println("-------");

    }

    public static void tt() {
        Map<String, Integer> conf = Maps.newHashMap();
        conf.put("nickname", 100);
        conf.put("account", 703);
        JsonMasking.DEFAULT.addSetting(conf);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000; i++) {
            JsonMasking.DEFAULT.masking(json);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        start = System.currentTimeMillis();
        for (int i = 0; i < 1_000_000; i++) {
            JsonUtil.toList(json, Map.class);
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println();
    }
}
