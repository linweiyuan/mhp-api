package com.linweiyuan.mhp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.Contact
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Profile("dev")
@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun createRestApi() = Docket(DocumentationType.SWAGGER_2)
            .apiInfo(ApiInfoBuilder().title("怪物猎人金手指API（3rd HD ver.）").contact(Contact("linweiyuan", "http://www.linweiyuan.com", "root@linweiyuan.com")).build())
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.linweiyuan.mhp.api"))
            .build()
            //全局Authorization请求头
            .securitySchemes(arrayListOf(ApiKey(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION, "header")))
            .securityContexts(arrayListOf(SecurityContext.builder().securityReferences(arrayListOf(SecurityReference(HttpHeaders.AUTHORIZATION, arrayOf<AuthorizationScope>()))).build()))
}
