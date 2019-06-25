package brave.propagation;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author jingshouyan
 * #date 2018/11/3 23:19
 */
public class TtlCurrentTraceContext extends ThreadLocalCurrentTraceContext {
    private static final TransmittableThreadLocal<TraceContext> TTL = new TransmittableThreadLocal<>();

    public static CurrentTraceContext create() {
        return new ThreadLocalCurrentTraceContext(new Builder(), TTL);
    }

    public static CurrentTraceContext ttl() {
        return new TtlCurrentTraceContext();
    }

    public TtlCurrentTraceContext() {
        super(new Builder(), TTL);
    }

    public static CurrentTraceContext.Builder newBuilder() {
        return new Builder();
    }

    static final class Builder extends CurrentTraceContext.Builder {

        @Override
        public CurrentTraceContext build() {
            return new ThreadLocalCurrentTraceContext(this, TTL);
        }

        Builder() {
        }
    }
}
