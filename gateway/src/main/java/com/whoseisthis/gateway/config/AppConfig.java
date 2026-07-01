package com.whoseisthis.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class AppConfig implements WebFluxConfigurer {
    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer)
    {
        ReactivePageableHandlerMethodArgumentResolver resolver =
                new ReactivePageableHandlerMethodArgumentResolver();

        resolver.setMaxPageSize(100);
        resolver.setSizeParameterName("limit");
        configurer.addCustomResolver(resolver);
    }
}

