package com.alevel.bot.service.db;

import com.alevel.bot.model.entity.BotUser;
import com.alevel.bot.telegram.TelegramBotState;

import java.util.List;
import java.util.Optional;

public interface UserOperations {

    Optional<BotUser> getUserBotChatId(Long chatId);

    void changeBotStateByChatId(Long chatId, TelegramBotState state);

    void createUser(BotUser user);

    Optional<BotUser> getById(Long chatId);

    List<BotUser> findAll();
}
