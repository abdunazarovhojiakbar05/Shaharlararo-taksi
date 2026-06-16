package com.example.taxi_project.cofig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import io.swagger.v3.oas.models.parameters.Parameter;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Child Tracking API")
                        .version("1.0.0")
                        .description("Child tracking system"))

                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addParameters("X-Device-ID",    new io.swagger.v3.oas.models.parameters.Parameter().in("header").name("X-Device-ID").schema(new StringSchema()).description("Qurilma ID si"))
                        .addParameters("X-Device-Name",  new io.swagger.v3.oas.models.parameters.Parameter().in("header").name("X-Device-Name").schema(new StringSchema()).description("Qurilma nomi"))
                        .addParameters("X-Platform",     new io.swagger.v3.oas.models.parameters.Parameter().in("header").name("X-Platform").schema(new StringSchema()).description("android / ios"))
                        .addParameters("X-App-Version",  new io.swagger.v3.oas.models.parameters.Parameter().in("header").name("X-App-Version").schema(new StringSchema()).description("App versiyasi")));


    }



    @Bean
    public OperationCustomizer globalHeaders() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            operation.addParametersItem(
                    new io.swagger.v3.oas.models.parameters.Parameter()
                            .in("header")
                            .name("X-Device-ID")
                            .schema(new StringSchema())
                            .description("Qurilma ID si")
                            .required(false)
            );
            operation.addParametersItem(
                    new io.swagger.v3.oas.models.parameters.Parameter()
                            .in("header")
                            .name("X-Device-Name")
                            .schema(new StringSchema())
                            .description("Qurilma nomi")
                            .required(false)
            );
            operation.addParametersItem(
                    new io.swagger.v3.oas.models.parameters.Parameter()
                            .in("header")
                            .name("X-Platform")
                            .schema(new StringSchema())
                            .description("android / ios")
                            .required(false)
            );
            operation.addParametersItem(
                    new Parameter()
                            .in("header")
                            .name("X-App-Version")
                            .schema(new StringSchema())
                            .description("App versiyasi")
                            .required(false)
            );
            return operation;
        };
    }




    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("1. Auth")
                .pathsToMatch("/api/auth/**", "/api/v1/auth/**")
                .addOperationCustomizer(globalHeaders())
                .build();
    }

    @Bean
    public GroupedOpenApi parentApi() {
        return GroupedOpenApi.builder()
                .group("2. user")
                .pathsToMatch()
                .addOperationCustomizer(globalHeaders())
                .build();
    }


    @Bean
    public GroupedOpenApi childApi() {
        return GroupedOpenApi.builder()
                .group("3. driver")
                .pathsToMatch(

                )
                .addOperationCustomizer(globalHeaders())
                .build();
    }
/*
    @Bean
    public GroupedOpenApi locationApi() {
        return GroupedOpenApi.builder()
                .group("4. Location & Geofence")
                .pathsToMatch( )
                .addOperationCustomizer(globalHeaders())
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("5. Super Admin")
                .pathsToMatch("/api/super-admin/**")
                .addOperationCustomizer(globalHeaders())
                .build();
    }

*/



}