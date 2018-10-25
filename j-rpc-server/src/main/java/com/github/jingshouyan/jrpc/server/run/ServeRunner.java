package com.github.jingshouyan.jrpc.server.run;

import com.github.jingshouyan.jrpc.base.bean.ServerInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jingshouyan
 * #date 2018/10/24 20:48
 */
@Slf4j
public class ServeRunner {
    private static final long UPDATE_DELAY = 600;


    @Getter
    private State state = State.INIT;
    @Getter
    private ServerInfo serverInfo;

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
        return this;
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
