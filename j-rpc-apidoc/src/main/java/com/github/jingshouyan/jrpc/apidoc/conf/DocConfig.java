package com.github.jingshouyan.jrpc.apidoc.conf;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.httpclient.TracingHttpClientBuilder;
import brave.servlet.TracingFilter;
import brave.spring.webmvc.SpanCustomizingAsyncHandlerInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

/**
 * @author jingshouyan
 * #date 2018/11/2 13:49
 */
@Configuration
@Import(SpanCustomizingAsyncHandlerInterceptor.class)
public class DocConfig implements WebMvcConfigurer {

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