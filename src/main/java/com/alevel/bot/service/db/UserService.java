package com.alevel.bot.service.db;

import com.alevel.bot.model.entity.BotUser;
import com.alevel.bot.telegram.TelegramBotState;
import com.alevel.bot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service()
public class UserService implements UserOperations {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<BotUser> getUserBotChatId(Long chatId) {
        return userRepository.findBotUserByChatId(chatId);
    }

    @Override
    @Transactional
    public void changeBotStateByChatId(Long chatId, TelegramBotState state) {
        userRepository.changeBotStateByChatId(chatId, state);
    }

    @Override
    public void createUser(BotUser user) {
        userRepository.save(user);
    }

    @Override
    public Optional<BotUser> getById(Long id){
        return userRepository.findBotUserById(id);
    }

    @Override
    public List<BotUser> findAll() {
        return userRepository.findAll();
    }

}

