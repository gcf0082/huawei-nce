package com.huawei.nce.business.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI businessOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("华为NCE网管 - Business API")
                        .description("配置模板业务服务API")
                        .version("1.0.0")
                        .contact(new Contact().name("华为NCE")));
    }
}
