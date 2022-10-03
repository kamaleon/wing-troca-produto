package com.korporate.wing.trocaProduto.feign;

import com.korporate.spring.persistence.multitenancy.TenantContext;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class FeignInterceptor
{
    @Value("${feign.token}")
    private String jwt;
    @Value("${feign.api-path}")
    private String apiPath;
    @Value("${api.header.emissor}")
    private String headerEmissor;

    @Bean
    public RequestInterceptor requestInterceptor()
    {
        return requestTemplate -> {
            // coloca o prefixo do endpoint (/api/v1)
            String endpoint = requestTemplate.url();
            requestTemplate.request().requestTemplate().uri(apiPath + endpoint);

            // tenant
            requestTemplate.header(headerEmissor, TenantContext.getTenant());

            // JWT
            requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        };
    }

}