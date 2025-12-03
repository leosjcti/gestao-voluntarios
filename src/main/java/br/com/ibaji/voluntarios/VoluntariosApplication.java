package br.com.ibaji.voluntarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VoluntariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoluntariosApplication.class, args);
	}

}
