package com.devdad.book_worms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
// Only works for CreatedDate and LastModifiedDate by default
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class BookWormsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookWormsApplication.class, args);
	}
	
// @Bean
// 	public CommandLineRunner runner(RoleRepository roleRepository) {
// 		return args -> {
// 			if (roleRepository.findByName("USER").isEmpty()) {
// 				roleRepository.save(Role.builder().name("USER").build());
// 			}
// 		};
// 	}

}
