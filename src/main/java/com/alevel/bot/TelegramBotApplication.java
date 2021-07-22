package com.alevel.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class TelegramBotApplication {

	public static void main(String[] args) {
		System.setProperty("illegal-access", "deny");
		SpringApplication.run(TelegramBotApplication.class, args);
	}

}
