package com.github.jingshouyan.jrpc.base.protocol;

import com.github.jingshouyan.jrpc.base.thrift.TokenBean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;

import java.util.HashMap;
import java.util.Map;

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
        log.info("client writeMessageEnd");
        // 发送请求数据完成
        TokenBean tokenBean = new TokenBean();
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Test","this is test Header");
        tokenBean.setHeaders(headers);
        tokenBean.write(this);

    }

    @Override
    @SneakyThrows
    public void readMessageEnd() {
        // 接收响应数据完成
        log.info("client readMessageEnd");
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
