package com.whoseisthis.gateway.config;

import com.whoseisthis.gateway.auth.interfaces.client.AuthApiClient;
import com.whoseisthis.gateway.report.interfaces.client.ReportApiClient;
import com.whoseisthis.gateway.user.interfaces.client.UserApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ApiClientConfig {
    private final String token;
    private final String authServiceUrl;
    private final String userServiceUrl;
    private final String reportServiceUrl;

    public ApiClientConfig(
            @Value("${gateway-token}") String token,
            @Value("${auth-service-url}") String authServiceUrl,
            @Value("${user-service-url}") String userServiceUrl,
            @Value("${report-service-url}") String reportServiceUrl)
    {
        this.token = token;
        this.authServiceUrl = authServiceUrl;
        this.userServiceUrl = userServiceUrl;
        this.reportServiceUrl = reportServiceUrl;
    }

    @Bean
    public AuthApiClient authApiClient()
    {
        WebClient client = WebClient
                .builder()
                .baseUrl(authServiceUrl)
                .defaultHeader("X-Gateway-Token", token)
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(AuthApiClient.class);
    }

    @Bean
    public UserApiClient userApiClient()
    {
        WebClient client = WebClient
                .builder()
                .baseUrl(userServiceUrl)
                .defaultHeader("X-Gateway-Token", token)
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(UserApiClient.class);
    }

    @Bean
    public ReportApiClient reportApiClient()
    {
        WebClient client = WebClient
                .builder()
                .baseUrl(reportServiceUrl)
                .defaultHeader("X-Gateway-Token", token)
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(ReportApiClient.class);
    }
}
