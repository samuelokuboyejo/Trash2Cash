package com.trash2cash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Trash2CashApplication {

	public static void main(String[] args) {
		SpringApplication.run(Trash2CashApplication.class, args);
	}

}
