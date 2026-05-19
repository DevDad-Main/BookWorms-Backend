package com.devdad.book_worms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Devdad", email = "softwaredevdad@gmail.com", url = "https://github.com/DevDad-Main"), description = "OpenAPI Documentation for Spring Security", title = "OpenAPI Specification", version = "1.0", license = @License(name = "License name", url = "https://someurl.com"), termsOfService = "Terms Of Service"), servers = {
		@Server(description = "Local Environment", url = "http://localhost:8088/api/v1"),
		@Server(description = "Production Environment", url = "https://production-url.com")
}, security = {
		@SecurityRequirement(name = "bearerAuth")
})
@SecurityScheme(name = "bearerAuth", description = "JWT Auth Description", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenAPIConfig {

}
