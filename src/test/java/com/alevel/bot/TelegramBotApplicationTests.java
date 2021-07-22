package com.alevel.bot;

import com.alevel.bot.controller.BotController;
import com.alevel.bot.model.dto.ResponseContentType;
import com.alevel.bot.model.entity.BotUser;
import com.alevel.bot.model.entity.UploadedFile;
import com.alevel.bot.service.db.UploadedFileService;
import com.alevel.bot.service.db.UserService;
import com.alevel.bot.telegram.TelegramBotState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TelegramBotApplicationTests {
    @Test
    void contextLoads() {
    }
}
