package com.github.jingshouyan.jrpc.base.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2019/3/28 21:08
 */

public class ActionInterceptorHolder {
    private static final List<ActionInterceptor> CLIENT_INTERCEPTORS = new ArrayList<>();
    private static final List<ActionInterceptor> SERVER_INTERCEPTORS = new ArrayList<>();

    public static List<ActionInterceptor> getClientInterceptors() {
        return CLIENT_INTERCEPTORS;
    }

    public static List<ActionInterceptor> getServerInterceptors() {
        return SERVER_INTERCEPTORS;
    }

    public static void addClientInterceptor(ActionInterceptor interceptor) {
        CLIENT_INTERCEPTORS.add(interceptor);
        Collections.sort(CLIENT_INTERCEPTORS);
    }

    public static void addServerInterceptor(ActionInterceptor interceptor) {
        SERVER_INTERCEPTORS.add(interceptor);
        Collections.sort(SERVER_INTERCEPTORS);
    }
}
