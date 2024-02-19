package pro.sky.telebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelebotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelebotApplication.class, args);
	}

}
