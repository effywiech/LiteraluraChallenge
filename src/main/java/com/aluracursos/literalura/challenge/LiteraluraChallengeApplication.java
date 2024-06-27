package com.aluracursos.literalura.challenge;

import com.aluracursos.literalura.challenge.principal.Principal;
import com.aluracursos.literalura.challenge.repository.AutorRepository;
import com.aluracursos.literalura.challenge.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LiteraluraChallengeApplication implements CommandLineRunner {

	@Autowired
	private LibroRepository libroRepository;

	@Autowired
	private AutorRepository autorRepository;

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraChallengeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = applicationContext.getBean(Principal.class);
		principal.run();
	}
}
