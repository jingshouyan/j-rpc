package com.github.jingshouyan.jrpc.starter.trace;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.sampler.CountingSampler;
import com.github.jingshouyan.jrpc.base.action.ActionInterceptorHolder;
import com.github.jingshouyan.jrpc.starter.trace.aop.TracingSpanX;
import com.github.jingshouyan.jrpc.starter.trace.interceptor.ClientTrace;
import com.github.jingshouyan.jrpc.starter.trace.interceptor.ServerTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * #date 2018/11/2 17:58
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(TraceProperties.class)
public class TraceAutoConfiguration {

    @Resource
    private TraceProperties properties;
    @Value("${jrpc.server.name:}")
    private String jRpcName;
    @Value("${spring.application.name:}")
    private String appName;



    /**
     * Configuration for how to send spans to Zipkin
     * @return Sender
     */
    @Bean
    @ConditionalOnMissingBean(Sender.class)
    public Sender sender() {
        return OkHttpSender.create(properties.getEndpoint());
    }


    /**
     * Configuration for how to buffer spans into messages for Zipkin
     * @param sender sender
     * @return AsyncReporter
     */
    @Bean
    @ConditionalOnMissingBean(AsyncReporter.class)
    public AsyncReporter<Span> spanReporter(Sender sender) {
        return AsyncReporter.create(sender);
    }


    /**
     * Controls aspects of tracing such as the name that shows up in the UI
     * @param spanReporter spanReporter
     * @return Tracing
     */
    @Bean
    @ConditionalOnMissingBean(Tracing.class)
    public Tracing tracing(AsyncReporter<Span> spanReporter) {

        return Tracing.newBuilder()
                .localServiceName(tracingName())
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        // puts trace IDs into logs
                        .addScopeDecorator(MDCScopeDecorator.create())
                        .build()
                )
                .sampler(CountingSampler.create(properties.getRate()))
                .spanReporter(spanReporter).build();
    }

    /**
     * ServerTrace
     * @param tracing tracing
     * @return ServerTrace
     */
    @Bean
    @ConditionalOnMissingBean(ServerTrace.class)
    public ServerTrace serverTrace(Tracing tracing) {
        ServerTrace serverTrace = new ServerTrace(tracing, properties);
        ActionInterceptorHolder.addServerInterceptor(serverTrace);
        return serverTrace;
    }

    /**
     * clientTrace
     * @param tracing Tracing
     * @return clientTrace
     */
    @Bean
    @ConditionalOnMissingBean(ClientTrace.class)
    public ClientTrace clientTrace(Tracing tracing) {
        ClientTrace clientTrace = new ClientTrace(tracing, properties);
        ActionInterceptorHolder.addClientInterceptor(clientTrace);
        return clientTrace;
    }

    /**
     * tracingSpanX
     * @param tracing tracing
     * @return tracingSpanX
     */
    @Bean
    @ConditionalOnMissingBean(TracingSpanX.class)
    public TracingSpanX tracingSpanX(Tracing tracing) {
        return new TracingSpanX(tracing);
    }

    private String tracingName() {
        String tracingName = "zipkin-trace";
        if (!StringUtils.isEmpty(appName)) {
            tracingName = appName;
        }
        if (!StringUtils.isEmpty(jRpcName)) {
            tracingName = jRpcName;
        }
        if (!StringUtils.isEmpty(properties.getName())) {
            tracingName = properties.getName();
        }
        return tracingName;
    }


}
