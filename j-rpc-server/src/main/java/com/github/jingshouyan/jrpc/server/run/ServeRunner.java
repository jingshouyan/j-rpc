package com.github.jingshouyan.jrpc.server.run;

/**
 * @author jingshouyan
 * #date 2018/10/24 20:48
 */

public class ServeRunner {
    private static final long UPDATE_DELAY = 600;



    private State state = State.INIT;

    private ServeRunner(){

    }

    private static ServeRunner instance;

    public synchronized static ServeRunner getInstance(){
        if(instance ==null){
            instance = new ServeRunner();
        }
        return instance;
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
