package com.alevel.bot.model.entity;

import com.alevel.bot.telegram.TelegramBotState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "bot_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @Column(name = "bot_state", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TelegramBotState botState = TelegramBotState.READY;
}
