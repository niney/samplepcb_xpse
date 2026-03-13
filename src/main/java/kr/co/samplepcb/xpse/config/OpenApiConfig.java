package kr.co.samplepcb.xpse.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Token";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SamplePCB XPSE API")
                        .description("SamplePCB XPSE REST API 문서")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 인증 토큰")));
    }

    @Bean
    public GroupedOpenApi spApi() {
        return GroupedOpenApi.builder()
                .group("sp-api")
                .displayName("SP 주문/아이템/견적 API")
                .pathsToMatch("/api/spOrders/**", "/api/spItems/**", "/api/spPartnerOrders/**", "/api/spEstimates/**")
                .build();
    }

    @Bean
    public GroupedOpenApi pcbApi() {
        return GroupedOpenApi.builder()
                .group("pcb-api")
                .displayName("PCB 부품/아이템/종류/컬럼 API")
                .pathsToMatch("/api/pcbParts/**", "/api/pcbItem/**", "/api/pcbKind/**", "/api/pcbColumn/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .displayName("전체 API")
                .pathsToMatch("/api/**", "/")
                .build();
    }
}
