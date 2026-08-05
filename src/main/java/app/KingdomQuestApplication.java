package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class KingdomQuestApplication {

	public static void main(String[] args) {
		SpringApplication.run(KingdomQuestApplication.class, args);
	}

}
