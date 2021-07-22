package com.alevel.bot.service.util;

import com.alevel.bot.telegram.TelegramBotState;
import com.alevel.bot.model.entity.BotUser;
import com.alevel.bot.service.db.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramService {

    private final UserService service;

    @Autowired
    public TelegramService(UserService service) {
        this.service = service;
    }

    public List<InlineKeyboardButton> getMediaFormats() {
        InlineKeyboardButton mp3Button = new InlineKeyboardButton().setText("mp3").setCallbackData("mp3");
        InlineKeyboardButton mp4Button = new InlineKeyboardButton().setText("mp4").setCallbackData("mp4");
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        buttonsRow.add(mp3Button);
        buttonsRow.add(mp4Button);
        return buttonsRow;
    }

    public InlineKeyboardMarkup createBlockButtons(List<InlineKeyboardButton> buttonsRow) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(buttonsRow);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public List<InlineKeyboardButton> getVideoFormatsButtons() {
        InlineKeyboardButton p360Button = new InlineKeyboardButton().setText("360p").setCallbackData("360p");
        InlineKeyboardButton p720Button = new InlineKeyboardButton().setText("720p").setCallbackData("720p");
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(p360Button);
        buttons.add(p720Button);
        return buttons;
    }

    public void createUserIfNotExist(Long chatId, String userName) {
        BotUser newUser = new BotUser();
        newUser.setUserName(userName);
        newUser.setBotState(TelegramBotState.READY);
        newUser.setChatId(chatId);
        service.createUser(newUser);
    }

}
