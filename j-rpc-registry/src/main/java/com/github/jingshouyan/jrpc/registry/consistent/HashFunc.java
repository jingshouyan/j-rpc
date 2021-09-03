package com.github.jingshouyan.jrpc.registry.consistent;

/**
 * HashFunc
 *
 * @author jingshouyan
 * 2021-09-03 13:27
 **/
@FunctionalInterface
public interface HashFunc {

    /**
     * 获取hash
     *
     * @param key key
     * @return hash
     */
    int hash(String key);

    /**
     * FNV1_32_HASH算法
     */
    HashFunc FNV1_32_HASH = key -> {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash ^ key.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    };
}
