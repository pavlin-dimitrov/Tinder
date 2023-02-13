package com.volasoftware.tinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class TinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TinderApplication.class, args);
	}

}
