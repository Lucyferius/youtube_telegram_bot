package com.alevel.bot.telegram;

import com.alevel.bot.cache.RequestsStorage;
import com.alevel.bot.exceptions.InvalidInputException;
import com.alevel.bot.exceptions.YoutubeBotExceptions;
import com.alevel.bot.model.dto.Request;
import com.alevel.bot.model.dto.Response;
import com.alevel.bot.model.dto.ResponseContentType;
import com.alevel.bot.model.entity.UploadedFile;
import com.alevel.bot.model.entity.BotUser;
import com.alevel.bot.service.db.UploadedFileService;
import com.alevel.bot.service.db.UserService;
import com.alevel.bot.service.message.ReplyMessageService;
import com.alevel.bot.service.util.DownloadedMediaCleanerService;
import com.alevel.bot.service.util.TelegramService;
import com.alevel.bot.service.message.UserInputParser;
import com.alevel.bot.service.util.UserResponseProcessor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Getter
@Setter
public class YoutubeTelegramBot extends TelegramWebhookBot {

    private Logger logger = LoggerFactory.getLogger(YoutubeTelegramBot.class);

    private String botPath;
    private String botUsername;
    private String botToken;

    @Autowired
    private UserService userService;

    @Autowired
    private UploadedFileService uploadedFileService;

    @Autowired
    private UserInputParser userInputParser;

    @Autowired
    private UserResponseProcessor userResponseProcessor;

    @Autowired
    private ReplyMessageService messageService;

    @Autowired
    private TelegramService facade;

    @Autowired
    private DownloadedMediaCleanerService mediaCleanerService;

    @Autowired
    public RequestsStorage storage;

    public YoutubeTelegramBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            logger.info("Process update from chatId:" + chatId);
            Optional<BotUser> findUser = userService.getUserBotChatId(chatId);
            if (findUser.isPresent()) {
                BotUser user = findUser.get();
                processUpdate(user, update.getMessage());
            } else {
                facade.createUserIfNotExist(chatId, update.getMessage().getChat().getUserName());
                logger.info("Create user with chatId:" + chatId);
                try {
                    execute(messageService.getReplyMessage(chatId, "response.info"));
                } catch (TelegramApiException e) {
                    logger.error("Error while executing message ", e);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String answer = update.getCallbackQuery().getData();

            try {
                Request currentRequest = storage.getCurrentRequest(chatId);
                if (storage.isRequestPresent(chatId)) {
                    if (!currentRequest.isProcessing())
                        processCallBack(chatId, answer, currentRequest);
                }
            } catch (TelegramApiException | YoutubeBotExceptions e) {
                logger.error("Error while executing message ", e);
            }
        }
        return null;
    }

    private void processUpdate(BotUser user, Message message) {
        try {
            if (message.getText().equals("/start") || message.getText().equals("/info") ) {
                execute(messageService.getReplyMessage(user.getChatId(), "response.info"));
            } else if (user.getBotState() == TelegramBotState.READY && !message.getText().isEmpty()) {
                botIsReadyToProcessUrl(user.getChatId(), message);
            } else if (user.getBotState() == TelegramBotState.BUSY && message.getText().equals("/stop")) {
                execute(messageService.getReplyMessage(user.getChatId(), "response.stopDownloading"));
                execute(messageService.getReplyMessage(user.getChatId(), "response.infoAgain"));
                userService.changeBotStateByChatId(user.getChatId(), TelegramBotState.READY);
                userResponseProcessor.stopDownload();
            } else if (user.getBotState() == TelegramBotState.BUSY) {
                execute(messageService.getReplyMessage(user.getChatId(), "response.wait"));
            }

        } catch (TelegramApiException | InvalidInputException ex) {
            ex.printStackTrace();
        }
    }

    private void botIsReadyToProcessUrl(Long chatId, Message message) throws TelegramApiException, InvalidInputException {
        userService.changeBotStateByChatId(chatId, TelegramBotState.BUSY);
        logger.info("Change bot state to BUSY for chatId: " + chatId);

        String videoId = userInputParser.getYouTubeVideoId(message.getText());

        if (videoId == null) {
            execute(messageService.getReplyMessage(chatId, "response.invalidInput"));
            execute(messageService.getReplyMessage(chatId, "response.infoAgain"));
            logger.info("Invalid url from user, input: " + message.getText());
            userService.changeBotStateByChatId(chatId, TelegramBotState.READY);
            logger.info("Change bot state to READY for chatId: " + chatId);
        } else {
            execute(messageService.getReplyMessage(chatId, "response.fileFound"));
            Request userRequest = new Request();
            userRequest.setVideoId(videoId);
            storage.updateRequest(chatId, userRequest);
            logger.info("Put request to map with chatId: " + chatId + " videoId: " + videoId);
            SendMessage choseFormatMessage = new SendMessage(chatId, messageService.getReplyText("response.choseFormat"));
            choseFormatMessage.setReplyMarkup(facade.createBlockButtons(facade.getMediaFormats()));
            execute(choseFormatMessage);
        }
    }

    public void processCallBack(Long chatId, String message, Request userRequest) throws TelegramApiException, YoutubeBotExceptions {
        switch (message) {
            case "mp3": {
                userRequest.setFormat(ResponseContentType.mp3);
                storage.updateRequest(chatId, userRequest);
                sendFileInFormat(chatId, 0, userRequest);
                break;
            }
            case "mp4": {
                logger.info("Callback mp4 from chatId: " + chatId);
                SendMessage choseFormatMessage = new SendMessage(chatId, messageService.getReplyText("response.choseQuality"));
                userRequest.setFormat(ResponseContentType.mp4);
                storage.updateRequest(chatId, userRequest);
                choseFormatMessage.setReplyMarkup(facade.createBlockButtons(facade.getVideoFormatsButtons()));
                execute(choseFormatMessage);
                break;
            }
            case "360p": {
                logger.info("Callback 360p from chatId: " + chatId);
                sendFileInFormat(chatId, 18, userRequest);
                break;
            }
            case "720p": {
                logger.info("Callback 720p from chatId: " + chatId);
                sendFileInFormat(chatId, 22, userRequest);
                break;
            }
        }
    }

    public void sendFileInFormat(long chatId, int code, Request userRequest) throws TelegramApiException {
        try {
            if (storage.getCurrentRequest(chatId) == null) {
                throw new YoutubeBotExceptions();
            }
            if (checkIfFileAlreadyExist(chatId, userRequest)) {
                logger.info("Video with id: " + userRequest.getVideoId() + " doesn`t exist in db, start creating");
                if (userRequest.getFormat().equals(ResponseContentType.mp4))
                    userRequest.setQualityCode(code);

                userRequest.setProcessing(true);

                storage.updateRequest(chatId, userRequest);
                execute(messageService.getReplyMessage(chatId, "response.prepareToLoad"));
                Response userResponse = userResponseProcessor.processResponse(userRequest);

                execute(messageService.getReplyMessage(chatId, "response.beginLoading"));

                logger.info("Begin loading file with id: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());

                if (mediaCleanerService.getFileSize(userRequest) >= 50) {
                    execute(messageService.getReplyMessage(chatId, "response.fileIsTooBig"));
                    mediaCleanerService.clean(userRequest, true);
                    throw new YoutubeBotExceptions();
                }
                uploadFileInTelegram(chatId, userRequest, userResponse);
                mediaCleanerService.clean(userRequest, false);
                execute(messageService.getReplyMessage(chatId, "response.infoAgain"));
            }

        } catch (YoutubeBotExceptions e) {
            try {
                mediaCleanerService.clean(userRequest, true);
                logger.info("Can`t execute format mp4 for video : " + userRequest.getVideoId() + " with quality " + userRequest.getQualityCode());
                if(storage.getCurrentRequest(chatId).isProcessing())
                    execute(messageService.getReplyMessage(chatId, "response.choseAnotherFormat"));
            } catch (TelegramApiException telegramApiException) {
                telegramApiException.printStackTrace();
            }
        }
        storage.removeRequest(chatId);
        userService.changeBotStateByChatId(chatId, TelegramBotState.READY);
        logger.info("Change bot state to READY for chatId: " + chatId);
    }

    private void uploadFileInTelegram(Long chatId, Request userRequest, Response userResponse) throws TelegramApiException {
        String telegramFileId;
        if (userRequest.getFormat().equals(ResponseContentType.mp4)) {
            SendVideo video = new SendVideo();
            video.setChatId(chatId);
            video.setVideo(userResponse.getName(), userResponse.getContentStream());
            telegramFileId = execute(video).getVideo().getFileId();
        } else {
            SendAudio audio = new SendAudio();
            audio.setChatId(chatId);
            audio.setAudio(userResponse.getName(), userResponse.getContentStream());
            telegramFileId = execute(audio).getAudio().getFileId();
        }
        logger.info("Load file with id: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());
        UploadedFile uploadedFile = new UploadedFile(userResponse.getName(), telegramFileId, userResponse.getContentType());
        uploadedFileService.createFile(uploadedFile);
        logger.info("Save file with id: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());

    }

    private boolean checkIfFileAlreadyExist(Long chatId, Request userRequest) throws TelegramApiException {
        UploadedFile uploadedFile = uploadedFileService.getFileByVideoIdAndType(userRequest.getVideoId(), userRequest.getFormat());

        if (uploadedFile != null) {
            logger.info("Finding file in db with: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());
            execute(messageService.getReplyMessage(chatId, "response.beginLoading"));
            try {
                if (uploadedFile.getType() == ResponseContentType.mp3) {
                    SendAudio audio = new SendAudio();
                    audio.setChatId(chatId);
                    audio.setAudio(uploadedFile.getTelegramFileId());
                    execute(audio);
                } else {
                    SendVideo video = new SendVideo();
                    video.setChatId(chatId);
                    video.setVideo(uploadedFile.getTelegramFileId());
                    execute(video);
                }
            } catch (TelegramApiException e) {
                uploadedFileService.deleteFile(uploadedFile.getTelegramFileId());
                return true;
            }
            logger.info("Load file in telegram file with id: " + userRequest.getVideoId() + " format: " + userRequest.getFormat() + " quality: " + userRequest.getQualityCode());
            execute(messageService.getReplyMessage(chatId, "response.infoAgain"));
            return false;
        }
        return true;
    }
}


