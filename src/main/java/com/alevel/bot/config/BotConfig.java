package com.alevel.bot.config;

import com.alevel.bot.telegram.YoutubeTelegramBot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotConfig {
    //configuration pulling properties from application.properties

    //name received during registration
    private String botUserName;
    private String botToken;
    private String botPath;

    private String proxyHost;

    @Bean
    public YoutubeTelegramBot getConfiguredTelegramBot() {
        DefaultBotOptions options = ApiContext
                .getInstance(DefaultBotOptions.class);

        options.setProxyHost(proxyHost);

        YoutubeTelegramBot instance = new YoutubeTelegramBot(options);
        instance.setBotUsername(botUserName);
        instance.setBotToken(botToken);
        instance.setBotPath(botPath);

        return instance;
    }
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}