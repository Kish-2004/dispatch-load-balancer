
package com.logistics.dispatch.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public GlobalOpenApiCustomizer railwayServerUrlCustomizer() {
        return openApi -> openApi.setServers(
            List.of(
                new Server()
                    .url("https://dispatch-load-balancer-production-d54b7633.up.railway.app")
                    .description("Railway Production Server")
            )
        );
    }
}
