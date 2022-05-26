package com.example.demo.configuration;

import com.example.demo.cache.CallbackCache;
import com.example.demo.cache.CallbackCacheImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * In Memory Cache, modelled with Spring Singleton scope
 */
@Configuration
public class BeanConfig {

    @Bean
    @Scope(SCOPE_SINGLETON)
    public CallbackCache appScopeInMemCache() {
        return new CallbackCacheImpl();
    }
}
