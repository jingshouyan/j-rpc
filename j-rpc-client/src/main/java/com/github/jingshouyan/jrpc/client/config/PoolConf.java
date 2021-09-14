package com.github.jingshouyan.jrpc.client.config;

import lombok.Data;

/**
 * @author jingshouyan
 * 2021-09-03 14:11
 **/
@Data
public class PoolConf {
    private int minIdle = 10;
    private int maxIdle = 200;
    private int maxTotal = 500;


    private boolean testOnCreate = false;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean testWhileIdle = false;
}
