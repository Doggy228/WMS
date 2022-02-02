package edu.doggy228.loyaltyexch.wms;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "edu.doggy228.loyaltyexch.wms")
public class WMSApplication {

	public static void main(String[] args) {
		SpringApplication.run(WMSApplication.class, args);
	}

	@Bean
	public OpenAPI wmsOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Wallet Management System API")
						.description("Гаманець для систем лояльності.")
						.version("v0.1.1")
						.license(new License().name("Apache 2.0").url("http://springdoc.org")))
				.components(new Components()
						.addSecuritySchemes("bearer-key",
								new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT").description("User access token.")));
	}

	@Bean
	public GroupedOpenApi walletOpenApi() {
		return GroupedOpenApi.builder()
				.group("Wallet API")
				.packagesToScan("edu.doggy228.loyaltyexch.wms.api.v1.wallet")
				.build();
	}

	@Bean
	public GroupedOpenApi adminOpenApi() {
		return GroupedOpenApi.builder()
				.group("Administration API")
				.packagesToScan("edu.doggy228.loyaltyexch.wms.api.v1.admin")
				.build();
	}

	@Bean
	public GroupedOpenApi loyaltyOpenApi() {
		return GroupedOpenApi.builder()
				.group("Loyalty Management API")
				.packagesToScan("edu.doggy228.loyaltyexch.wms.api.v1.loyalty")
				.build();
	}
}
