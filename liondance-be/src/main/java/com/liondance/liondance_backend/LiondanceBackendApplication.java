//test
package com.liondance.liondance_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LiondanceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiondanceBackendApplication.class, args);
	}

}
