package com.dsr.practice.testingsystem.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";

    @Bean
    public Docket swaggerSpringfoxDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(apiKey()))
                .select()
                .paths(regex(DEFAULT_INCLUDE_PATTERN))
                .build()
                .apiInfo(apiDetails());
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                "Testing system API",
                "Current page has all api documentation.",
                "0.1",
                "Free to use",
                new Contact("Anonymous", "url", "email"),
                "API License",
                "url",
                Collections.emptyList());
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", HttpHeaders.AUTHORIZATION, "header");
    }

    private SecurityContext securityContext() {
        AuthorizationScope[] authorizationScopes = {new AuthorizationScope("default", "default")};
        List<SecurityReference> securityReferences = Collections.singletonList(
                new SecurityReference("JWT", authorizationScopes));
        return SecurityContext.builder().securityReferences(securityReferences).build();
    }
}