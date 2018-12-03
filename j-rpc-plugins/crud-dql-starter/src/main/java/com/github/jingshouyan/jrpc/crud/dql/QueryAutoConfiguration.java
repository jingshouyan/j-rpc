package com.github.jingshouyan.jrpc.crud.dql;


import com.github.jingshouyan.jrpc.crud.dql.method.Retrieve;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * 12/3/18 4:38 PM
 */
@Configuration
public class QueryAutoConfiguration {
    @Resource
    private ApplicationContext ctx;


    @Bean
    @ConditionalOnMissingBean(Retrieve.class)
    public Retrieve retrieve(ApplicationContext ctx){
        return new Retrieve(ctx);
    }
}
