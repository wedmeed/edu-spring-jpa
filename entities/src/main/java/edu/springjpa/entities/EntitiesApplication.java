package edu.springjpa.entities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class EntitiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(EntitiesApplication.class, args);
	}

}
