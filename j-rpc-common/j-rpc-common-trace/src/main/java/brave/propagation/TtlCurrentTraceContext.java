package brave.propagation;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author jingshouyan
 * #date 2018/11/3 23:19
 */
public class TtlCurrentTraceContext extends ThreadLocalCurrentTraceContext {
    private static final TransmittableThreadLocal<TraceContext> TTL = new TransmittableThreadLocal<>();

    public static CurrentTraceContext create() {
        return new TtlCurrentTraceContext();
    }

    public TtlCurrentTraceContext() {
        super(new ThreadLocalCurrentTraceContext.Builder(TTL));
    }

}
