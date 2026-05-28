package com.devdad.book_worms.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.ORIGIN;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class BeansConfig {

	@Value("${application.cors.origins}")
	private List<String> allowedOrigins;

	@Bean
	public AuditorAware<String> auditorAware() {
		return new ApplicationAuditAware();
	}

	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(allowedOrigins);
		config.setAllowedHeaders(Arrays.asList(
				ORIGIN,
				CONTENT_TYPE,
				ACCEPT,
				AUTHORIZATION));
		config.setAllowedMethods(Arrays.asList(
				"GET", "POST", "DELETE", "PUT", "PATCH"));

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
