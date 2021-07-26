package com.alevel.bot.repository;

import com.alevel.bot.model.entity.BotUser;
import com.alevel.bot.telegram.TelegramBotState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<BotUser, Long> {

    Optional<BotUser> findBotUserByChatId(Long chatId);

    @Query("update BotUser u set u.botState = :state where u.chatId = :chatId")
    @Modifying
    void changeBotStateByChatId(Long chatId, TelegramBotState state);

    Optional<BotUser> findBotUserById(Long id);
}
