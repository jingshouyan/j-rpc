package com.github.jingshouyan.jrpc.client.transport;

import lombok.Data;
import org.apache.thrift.transport.TTransport;

/**
 * @author jingshouyan
 * @date 2018/4/18 10:59
 */
@Data
public class Transport {
    private String key;
    private TTransport tTransport;

    public boolean isOpen(){
        if(null == tTransport){
            return false;
        }
        return tTransport.isOpen();
    }
}
