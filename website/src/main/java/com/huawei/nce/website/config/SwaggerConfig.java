package com.huawei.nce.website.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI websiteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("华为NCE网管 - Website API")
                        .description("配置模板管理前端服务API")
                        .version("1.0.0")
                        .contact(new Contact().name("华为NCE")));
    }
}
