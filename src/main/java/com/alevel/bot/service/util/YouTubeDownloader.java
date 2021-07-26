package com.alevel.bot.service.util;


import com.alevel.bot.exceptions.YoutubeBotExceptions;
import com.alevel.bot.model.dto.Request;
import com.alevel.bot.model.dto.ResponseContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
public class YouTubeDownloader {

    private final Logger logger = LoggerFactory.getLogger(YouTubeDownloader.class);

    private final static String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=%s";
    private final static String STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS = "youtube-dl -f %s ";
    private final static String STANDARD_DL_PATH_OPTION =" -o %s/%s.%s ";

    private final static String TERMINAL = "cmd.exe";
    private final static String END_COMMAND = "/c";

    private Process process;

    private final FolderManagerService folderManagerService;

    @Autowired
    public YouTubeDownloader(FolderManagerService folderManagerService) {
        this.folderManagerService = folderManagerService;
        folderManagerService.createDirectoryForFiles();
    }

    public void download(Request request) throws YoutubeBotExceptions {
        String[] commands = new String[3];
        int exitCode;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            logger.info("Start executing command to cmd.exe ");
            commands[0] = TERMINAL;
            commands[1] = END_COMMAND;
            commands[2] = buildCommand(request);
            builder.command(commands);
            builder.redirectErrorStream(true);

            process = builder.start();

            logger.info("Run process: " + process.info());

            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                logger.error("Error in process: " + process.info(), e);
                throw new YoutubeBotExceptions("Couldn`t send wanted quality: " + request.getQualityCode());
            }

            if (exitCode > 0) {
                logger.error("Error in process: " + process.info());
                stopDownloading();
                throw new YoutubeBotExceptions("Problem occurred while processing input stream");
            }

        } catch (Exception e) {
            throw new YoutubeBotExceptions();
        }
    }

    // IN PROGRESS
    public void stopDownloading() {
        process.destroy();
    }

    private String buildCommand(Request request){
        String finalFormat;
        if(request.getFormat().equals(ResponseContentType.mp3)) {
            finalFormat = String.format(STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS, "bestaudio");
        }else {
            finalFormat = String.format(STANDARD_DL_COMMAND_WITH_FORMAT_OPTIONS, request.getQualityCode());
        }
        return finalFormat +
                String.format(STANDARD_DL_PATH_OPTION,
                        folderManagerService.getPath(), request.getVideoId(), request.getFormat().name())
                + String.format(YOUTUBE_VIDEO_URL, request.getVideoId());
    }
}
