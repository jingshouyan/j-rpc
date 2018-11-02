package com.github.jingshouyan.jrpc.apidoc.conf;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.http.HttpTracing;
import brave.httpclient.TracingHttpClientBuilder;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.servlet.TracingFilter;
import brave.spring.webmvc.SpanCustomizingAsyncHandlerInterceptor;
import javax.servlet.Filter;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author jingshouyan
 * #date 2018/11/2 13:49
 */
@Configuration
@Import(SpanCustomizingAsyncHandlerInterceptor.class)
public class DocConfig implements WebMvcConfigurer {

    /** Configuration for how to send spans to Zipkin */
    @Bean
    Sender sender() {
        return OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
    }

    /** Configuration for how to buffer spans into messages for Zipkin */
    @Bean AsyncReporter<Span> spanReporter() {
        return AsyncReporter.create(sender());
    }

    /** Controls aspects of tracing such as the name that shows up in the UI */
    @Bean Tracing tracing() {
        return Tracing.newBuilder()
                .localServiceName("api-doc")
                .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        // puts trace IDs into logs
                        .addScopeDecorator(MDCScopeDecorator.create())
                        .build()
                )
                .spanReporter(spanReporter()).build();
    }

    /** decides how to name and tag spans. By default they are named the same as the http method. */
    @Bean HttpTracing httpTracing(Tracing tracing) {
        return HttpTracing.create(tracing);
    }

    /** Creates server spans for http requests */
    @Bean
    Filter tracingFilter(HttpTracing httpTracing) {
        return TracingFilter.create(httpTracing);
    }

    @Bean
    RestTemplateCustomizer useTracedHttpClient(HttpTracing httpTracing) {
        final CloseableHttpClient httpClient = TracingHttpClientBuilder.create(httpTracing).build();
        return new RestTemplateCustomizer() {
            @Override public void customize(RestTemplate restTemplate) {
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
            }
        };
    }

    @Autowired
    SpanCustomizingAsyncHandlerInterceptor webMvcTracingCustomizer;

    /** Decorates server spans with application-defined web tags */
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webMvcTracingCustomizer);
    }
}