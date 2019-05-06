package com.github.jingshouyan.jrpc.client.transport;

import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.transport.TNonblockingSocket;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author jingshouyan
 * @date 2018/4/18 10:59
 */
@Data@Slf4j
public class Transport implements Closeable {
    private static final int LOOP = 2;
    private String key;

    private Jrpc.AsyncClient asyncClient;
    private TNonblockingSocket nonblockingSocket;

    public boolean isOpen(){

        long start = System.nanoTime();
        if(nonblockingSocket == null) {
            return false;
        }
        try {
            for (int i = 0; i < LOOP; i++) {
                nonblockingSocket.getSocketChannel().socket().sendUrgentData(0xFF);
            }
            log.trace("test socket connect : open,use: {} ns",System.nanoTime() - start);
            return true;
        } catch (IOException e) {
            log.warn("test socket connect : closed,use: {} ns",System.nanoTime() - start);
            return false;
        }
    }

    @Override
    public void close() {
        if(nonblockingSocket.isOpen()) {
            nonblockingSocket.close();
        }
    }
}
