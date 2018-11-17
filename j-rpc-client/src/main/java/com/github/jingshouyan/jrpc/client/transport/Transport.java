package com.github.jingshouyan.jrpc.client.transport;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 * @author jingshouyan
 * @date 2018/4/18 10:59
 */
@Data@Slf4j
public class Transport implements Closeable {

    private String key;
    private TTransport tTransport;
    private Socket socket;

    public boolean isOpen(){
        long start = System.nanoTime();
        if(socket == null) {
            return false;
        }
        try {
            for (int i = 0; i < 1; i++) {
                socket.sendUrgentData(0xFF);
            }

            log.debug("test socket connect : open,use: {} ns",System.nanoTime() - start);
            return true;
        } catch (IOException e) {
            log.warn("test socket connect : closed,use: {} ns",System.nanoTime() - start);
            return false;
        }
    }

    @Override
    public void close() {
        if(tTransport.isOpen()) {
            tTransport.close();
        }
    }
}
