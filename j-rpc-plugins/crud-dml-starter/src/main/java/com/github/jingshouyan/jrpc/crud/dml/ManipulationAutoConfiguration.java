package com.github.jingshouyan.jrpc.crud.dml;

import com.github.jingshouyan.jrpc.crud.dml.method.Create;
import com.github.jingshouyan.jrpc.crud.dml.method.Delete;
import com.github.jingshouyan.jrpc.crud.dml.method.Update;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * 12/3/18 5:10 PM
 */
@Configuration
@EnableConfigurationProperties({ManipulationProperties.class})
public class ManipulationAutoConfiguration {

    @Resource
    private ManipulationProperties properties;


    @Bean
    @ConditionalOnMissingBean(Create.class)
    public Create create(ApplicationContext ctx){
        return new Create(ctx,properties);
    }

    @Bean
    @ConditionalOnMissingBean(Update.class)
    public Update update(ApplicationContext ctx){
        return new Update(ctx,properties);
    }

    @Bean
    @ConditionalOnMissingBean(Delete.class)
    public Delete delete(ApplicationContext ctx){
        return new Delete(ctx,properties);
    }
}
