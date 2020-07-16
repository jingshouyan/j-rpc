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
public class TBinaryProtocolServer extends TBinaryProtocol {

    public TBinaryProtocolServer(TTransport trans) {
        super(trans);
    }

    public TBinaryProtocolServer(TTransport trans, boolean strictRead, boolean strictWrite) {
        super(trans, strictRead, strictWrite);
    }

    public TBinaryProtocolServer(TTransport trans, long stringLengthLimit, long containerLengthLimit) {
        super(trans, stringLengthLimit, containerLengthLimit);
    }

    public TBinaryProtocolServer(TTransport trans, long stringLengthLimit, long containerLengthLimit, boolean strictRead, boolean strictWrite) {
        super(trans, stringLengthLimit, containerLengthLimit, strictRead, strictWrite);
    }


    @Override
    @SneakyThrows
    public void readMessageEnd() {
        log.info("server readMessageEnd");
        // 接受请求数据完成
        TokenBean tokenBean = new TokenBean();
        if(trans_.getBytesRemainingInBuffer()>0){
            tokenBean.read(this);
            log.info("readMessageEnd,tokenBean: {}" , tokenBean);
        }

    }

    @Override
    @SneakyThrows
    public void writeMessageEnd() {
        // 发送响应数据完成
        log.info("server writeMessageEnd");
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
            return new TBinaryProtocolServer(trans, stringLengthLimit_, containerLengthLimit_, strictRead_, strictWrite_);
        }
    }

}
