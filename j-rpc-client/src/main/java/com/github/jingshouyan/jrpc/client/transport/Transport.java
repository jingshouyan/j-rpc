package com.github.jingshouyan.jrpc.client.transport;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.IOException;
import java.net.Socket;

/**
 * @author jingshouyan
 * @date 2018/4/18 10:59
 */
@Data@Slf4j
public class Transport {

    private String key;
    private TTransport tTransport;
    private Socket socket;

    public boolean isOpen(){
        if(socket == null) {
            return false;
        }
        try {
            socket.sendUrgentData(0xFF);
            log.info("test socket connect : open");
            return true;
        } catch (IOException e) {
            log.warn("test socket connect : closed");
            return false;
        }
    }
}
