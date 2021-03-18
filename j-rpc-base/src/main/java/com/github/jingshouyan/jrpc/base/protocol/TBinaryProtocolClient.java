package com.github.jingshouyan.jrpc.base.protocol;

import com.github.jingshouyan.jrpc.base.thrift.ThriftHeaders;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;

import java.util.Map;

/**
 * TBinaryProtocol 客户端
 *
 * @author jingshouyan
 * 2021-03-15 17:32
 **/
@Slf4j
public class TBinaryProtocolClient extends TBinaryProtocol {

    public TBinaryProtocolClient(TTransport trans) {
        super(trans);
    }

    public TBinaryProtocolClient(TTransport trans, boolean strictRead, boolean strictWrite) {
        super(trans, strictRead, strictWrite);
    }

    public TBinaryProtocolClient(TTransport trans, long stringLengthLimit, long containerLengthLimit) {
        super(trans, stringLengthLimit, containerLengthLimit);
    }

    public TBinaryProtocolClient(TTransport trans, long stringLengthLimit, long containerLengthLimit, boolean strictRead, boolean strictWrite) {
        super(trans, stringLengthLimit, containerLengthLimit, strictRead, strictWrite);
    }

    @Override
    @SneakyThrows
    public void writeMessageEnd() {
        Map<String, String> header = HeadManager.requestHeader();
        ThriftHeaders thriftHeaders = new ThriftHeaders();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (Strings.isNullOrEmpty(key) || Strings.isNullOrEmpty(value)) {
                log.warn("header key[{}] or value[{}] is empty", key, value);
                header.remove(key);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("append header : {}", header);
        }
        thriftHeaders.setHeader(header);
        thriftHeaders.write(this);
    }

    @Override
    public void readMessageEnd() {
        HeadManager.cleanRequestHeader();
    }

    /**
     * Factory
     */
    public static class Factory implements TProtocolFactory {
        protected long stringLengthLimit_;
        protected long containerLengthLimit_;
        protected boolean strictRead_;
        protected boolean strictWrite_;

        public Factory() {
            this(false, true);
        }

        public Factory(boolean strictRead, boolean strictWrite) {
            this(strictRead, strictWrite, -1, -1);
        }

        public Factory(long stringLengthLimit, long containerLengthLimit) {
            this(false, true, stringLengthLimit, containerLengthLimit);
        }

        public Factory(boolean strictRead, boolean strictWrite, long stringLengthLimit, long containerLengthLimit) {
            stringLengthLimit_ = stringLengthLimit;
            containerLengthLimit_ = containerLengthLimit;
            strictRead_ = strictRead;
            strictWrite_ = strictWrite;
        }

        @Override
        public TProtocol getProtocol(TTransport trans) {
            return new TBinaryProtocolClient(trans, stringLengthLimit_, containerLengthLimit_, strictRead_, strictWrite_);
        }
    }

}
