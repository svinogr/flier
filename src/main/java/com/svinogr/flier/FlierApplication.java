package com.svinogr.flier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = "com.svinogr.flier")
@EnableR2dbcRepositories
public class FlierApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlierApplication.class, args);
	}

}
