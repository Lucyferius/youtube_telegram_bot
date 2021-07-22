package com.alevel.bot.service;

import com.alevel.bot.model.dto.ResponseContentType;
import com.alevel.bot.model.entity.BotUser;
import com.alevel.bot.model.entity.UploadedFile;
import com.alevel.bot.service.db.UploadedFileService;
import com.alevel.bot.service.db.UserService;
import com.alevel.bot.telegram.TelegramBotState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class UserAndUploadedFileServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UploadedFileService uploadedFileService;

    private BotUser currentUser;

    private UploadedFile file;

    private final Random rand = new Random();

    @BeforeEach
    void setUp() {
        currentUser = new BotUser(randomId(), randomName(), randomChatId(), TelegramBotState.READY );
        file = new UploadedFile(randomId(), randomName(), randomName(), ResponseContentType.mp3);
    }

    @Test
    void createUser(){

        Long chatId = currentUser.getChatId();
        userService.createUser(currentUser);

        Optional<BotUser> savedUser = userService.getUserBotChatId(chatId);
        Assertions.assertNotNull(savedUser.get());
        Assertions.assertNotNull(userService.getById(savedUser.get().getId()));

        BotUser user2 = new BotUser(2L, randomName(), randomChatId(), TelegramBotState.READY);
        Assertions.assertDoesNotThrow(()->userService.createUser(user2));

        List<BotUser> users = userService.findAll();
        Assertions.assertFalse(users.isEmpty());
    }


    @Test
    void changeUserState(){
        userService.createUser(currentUser);

        Assertions.assertNotNull(userService.getById(currentUser.getId()));

        userService.changeBotStateByChatId(currentUser.getChatId(), TelegramBotState.BUSY);
        Optional<BotUser> modifyUser  = userService.getUserBotChatId(currentUser.getChatId());

        Assertions.assertNotNull(modifyUser);

        Assertions.assertEquals(TelegramBotState.BUSY, modifyUser.get().getBotState());
    }

    @Test
    void createFile(){

        uploadedFileService.createFile(file);

        UploadedFile savedFile = uploadedFileService.getById(file.getId());

        Assertions.assertNull(savedFile);

        String videoId = randomName();
        UploadedFile uploadedFileMp3 = new UploadedFile(
                randomId(),
                videoId,
                randomName(),
                ResponseContentType.mp3
        );

        uploadedFileService.createFile(uploadedFileMp3);

        UploadedFile differOne = uploadedFileService.getFileByVideoIdAndType(videoId, ResponseContentType.mp3);
        UploadedFile differTwo = uploadedFileService.getFileByVideoIdAndType(videoId, ResponseContentType.mp4);

        Assertions.assertNotNull(differOne);
        Assertions.assertNull(differTwo);

        UploadedFile uploadedFileMp4 = new UploadedFile(
                randomId(),
                videoId,
                randomName(),
                ResponseContentType.mp4
        );

        uploadedFileService.createFile(uploadedFileMp4);

    }


    private String randomName(){
        return UUID.randomUUID().toString();
    }

    private Long randomChatId(){
        return 100000000L + rand.nextInt(900000000);
    }

    private Long randomId(){
        return rand.nextLong();
    }
}
