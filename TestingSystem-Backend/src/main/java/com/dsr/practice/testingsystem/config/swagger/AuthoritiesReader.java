package com.dsr.practice.testingsystem.config.swagger;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@RequiredArgsConstructor
public class AuthoritiesReader implements OperationBuilderPlugin {

    private final DescriptionResolver descriptions;

    @Override
    public void apply(OperationContext context) {
        Optional<PreAuthorize> preAuthorizeAnnotation = context.findAnnotation(PreAuthorize.class);
        String roles = preAuthorizeAnnotation.isPresent() ? preAuthorizeAnnotation.get().value() : "for everyone";
        String resolvedDescription = descriptions.resolve(
                String.format("<b>Access Privileges & Rules</b>: %s", roles)
        );
        context.operationBuilder().notes(resolvedDescription);
    }

    @Override
    public boolean supports(@NonNull DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}
