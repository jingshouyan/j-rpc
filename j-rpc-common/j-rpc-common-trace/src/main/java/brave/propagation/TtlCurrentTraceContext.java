package brave.propagation;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author jingshouyan
 * #date 2018/11/3 23:19
 */
public class TtlCurrentTraceContext {
    private static final TransmittableThreadLocal<TraceContext> TTL = new TransmittableThreadLocal<>();

    public static ThreadLocalCurrentTraceContext.Builder newBuilder() {
        return new ThreadLocalCurrentTraceContext.Builder(TTL);
    }

}
