package com.devdad.book_worms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import com.devdad.book_worms.role.Role;
import com.devdad.book_worms.role.RoleRepository;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class BookWormsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookWormsApplication.class, args);
	}
	
@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(Role.builder().name("USER").build());
			}
		};
	}

}
