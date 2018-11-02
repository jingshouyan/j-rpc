package com.jing.test.config;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author jingshouyan
 * #date 2018/10/31 20:11
 */
//@Configuration
public class TestConfig {

    /** Configuration for how to send spans to Zipkin */
    @Bean Sender sender() {
        return OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
    }

    /** Configuration for how to buffer spans into messages for Zipkin */
    @Bean
    AsyncReporter<Span> spanReporter() {
        return AsyncReporter.create(sender());
    }

    /** Controls aspects of tracing such as the name that shows up in the UI */
    @Bean
    Tracing tracing() {
        return Tracing.newBuilder()
                .localServiceName("test")
                .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        // puts trace IDs into logs
                        .addScopeDecorator(MDCScopeDecorator.create())
                        .build()
                )
                .spanReporter(spanReporter()).build();
    }
}
