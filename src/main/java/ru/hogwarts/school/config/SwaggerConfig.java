package ru.hogwarts.school.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hogwarts School API")
                        .version("1.0.0")
                        .description("REST API для управления студентами и факультетами Хогвартса")
                        .contact(new Contact()
                                .name("Hogwarts School")
                                .email("admin@hogwarts.ru")
                        )
                );
    }
}