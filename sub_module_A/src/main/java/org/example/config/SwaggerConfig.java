package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                //swagger 기본 문서 설정
                .info(new Info().title("API ETC Service Docs").version("1.0.0")
                        .description("부가적인 서비스 API 문서입니다.")
                        .contact(
                            new Contact()
                                .name("haram")
                                .email("jmh667722@gmail.co.kr")
                        )
                        .license(
                            new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")
                        )
                );
    }

}


