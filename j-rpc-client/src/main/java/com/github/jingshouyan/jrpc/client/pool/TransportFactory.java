package com.github.jingshouyan.jrpc.client.pool;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.client.transport.Transport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


/**
 * @author jingshouyan
 * @date 2018/4/17 20:41
 */
@Slf4j
public class TransportFactory extends BasePooledObjectFactory<Transport> implements PooledObjectFactory<Transport> {

    private ServerInfo serverInfo;

    public TransportFactory(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public Transport create() {
        try {
            Transport transport = new Transport();
            TSocket socket = new TSocket(serverInfo.getHost(), serverInfo.getPort());
            socket.getSocket().setKeepAlive(true);
            socket.getSocket().setTcpNoDelay(true);
            socket.getSocket().setSoLinger(false, 0);
            socket.setTimeout(serverInfo.getTimeout());
            TTransport tTransport = new TFramedTransport(socket, serverInfo.getMaxReadBufferBytes());

            tTransport.open();
            log.info("client pool make object success. {}==>{}",serverInfo.getName(),serverInfo.key());
            transport.setKey(serverInfo.key());
            transport.setTTransport(tTransport);
            transport.setSocket(socket.getSocket());
            socket.getSocket().setOOBInline(true);
            return transport;
        } catch (Exception e) {
            log.warn("client pool make object error.",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PooledObject<Transport> wrap(Transport transport) {
        return new DefaultPooledObject<>(transport);
    }

    @Override
    public void destroyObject(PooledObject<Transport> p) throws Exception {
        Transport transport = p.getObject();
        if(transport != null){
            transport.close();
        }
    }

    @Override
    public boolean validateObject(PooledObject<Transport> p) {
        Transport transport = p.getObject();
        if(transport != null && transport.isOpen()){
            return true;
        }
        return false;
    }
}
