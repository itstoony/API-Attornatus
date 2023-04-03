package br.com.itstoony.attornatus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(appInfo());
    }

    private Info appInfo() {
        return new Info()
                .title("Person API")
                .description("API for Person management")
                .version("1.0")
                .termsOfService("https://swagger.io/terms/")
                .license(appLicense())
                .contact(appContact());
    }

    private License appLicense() {
        return new License()
                .name("Apache 2.0")
                .url("https://springdoc.org");
    }

    private Contact appContact() {
        return new Contact()
                .name("Tony Rene")
                .url("https://github.com/itstoony")
                .email("toonyrenner@gmail.com");
    }
}
