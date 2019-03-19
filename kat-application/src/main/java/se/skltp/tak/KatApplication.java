package se.skltp.tak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "se.skltp.tak,se.skltp.takcache")
public class KatApplication {

	public static void main(String[] args) {
		SpringApplication.run(KatApplication.class, args);
	}

}
