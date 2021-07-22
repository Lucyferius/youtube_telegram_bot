package com.alevel.bot.exceptions;

public class InvalidInputException extends YoutubeBotExceptions{
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidInputException(String message){
        super(message);
    }
}
