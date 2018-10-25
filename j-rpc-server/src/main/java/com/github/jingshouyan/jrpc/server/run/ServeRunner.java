package com.github.jingshouyan.jrpc.server.run;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import com.github.jingshouyan.jrpc.base.thrift.Jrpc;
import com.github.jingshouyan.jrpc.server.service.Rpc;
import com.github.jingshouyan.jrpc.server.service.impl.RpcImpl;
import com.github.jingshouyan.jrpc.server.thrift.server.factory.ServerFactory;
import com.github.jingshouyan.jrpc.server.thrift.server.register.Register;
import com.github.jingshouyan.jrpc.server.thrift.server.register.ZkRegister;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.server.TServer;

import java.util.concurrent.*;

/**
 * @author jingshouyan
 * #date 2018/10/24 20:48
 */
@Slf4j
public class ServeRunner {
    private static final long UPDATE_DELAY = 600;

    private static final ExecutorService SERVER_RUNNER_POOL = new ThreadPoolExecutor(1,
            1,0L,TimeUnit.MICROSECONDS,new SynchronousQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("server-runner-%d").build(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Getter
    private State state = State.INIT;
    @Getter
    private ServerInfo serverInfo;

    private TServer tserver;

    private Register register = new ZkRegister();

    private Rpc rpc;

    private ServeRunner(){ }

    @Getter
    private static ServeRunner instance = new ServeRunner();

    public ServeRunner setServerInfo(ServerInfo serverInfo) {
        if(this.serverInfo == null){
            log.info("set server info : {}",serverInfo);
            this.serverInfo = serverInfo;
        }else {
            log.warn("server info is already set.");
        }
        initServer();
        return this;
    }


    public ServeRunner setIface(Rpc rpc){
        if(this.rpc == null){
            log.info("set server impl: {}", rpc);
            this.rpc = rpc;
        }
        initServer();
        return this;
    }

    private void initServer() {
        if(serverInfo != null && rpc != null){
            this.tserver = ServerFactory.getServer().getServer(rpc,serverInfo);
        }
    }


    public void start(){
        SERVER_RUNNER_POOL.execute(()-> {
            while (true) {
                try {
                    if(!tserver.isServing()){
                        break;
                    }
                    log.info("waiting ...");
                    Thread.sleep(2000);
                }catch (Exception e){}
            }
            log.info("server run...");
            try{
                tserver.serve();
            }catch (Exception e){
                log.error("server start failed.",e);
                System.exit(-1);
            }
            log.info("server stop...");
        });
        register.register(tserver,serverInfo);
    }

    public void stop(){
        tserver.stop();
    }



    public static void main(String[] args) throws Exception {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setPort(9099);
        ServeRunner s = ServeRunner.getInstance().setIface(new RpcImpl()).setServerInfo(serverInfo);
        s.start();
        Thread.sleep(10000);
        s.stop();
        Thread.sleep(3000);
        s.start();
        Executors.newFixedThreadPool(1);
    }


    public enum State {
        INIT("init"),
        STARTING("starting"),
        RUNNING("running"),
        STOPPING("stopping"),
        STOPPED("stopped");
        private String name;

        private State(String name) {
            this.name = name;
        }
    }
}
