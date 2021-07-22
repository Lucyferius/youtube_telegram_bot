package com.alevel.bot.service.util;


import com.alevel.bot.exceptions.YoutubeBotExceptions;
import com.alevel.bot.model.dto.Request;
import com.alevel.bot.model.dto.ResponseContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service()
public class YouTubeDownloader {

    private final Logger logger = LoggerFactory.getLogger(YouTubeDownloader.class);

    private final static String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=%s";

    private final static String mp3Format = ".mp3 ";

    private final static String mp4Format = ".mp4 ";

    private final static String TERMINAL = "cmd.exe";

    private Process process;

    private StreamProcessExtractorService stdOutProcessor;

    private InputStream outStream;

    private final FolderManagerService folderManagerService;

    @Autowired
    public YouTubeDownloader(FolderManagerService folderManagerService) {
        this.folderManagerService = folderManagerService;
        folderManagerService.createDirectoryForFiles();
    }

    public void download(Request request) throws YoutubeBotExceptions {

        StringBuffer outBuffer = new StringBuffer();
        int exitCode;
        boolean isAudio = false;
        String currentFormat = mp4Format;

        try {

            if (request.getFormat().equals(ResponseContentType.mp3)) {
                isAudio = true;
                currentFormat = mp3Format;
            }

            ProcessBuilder builder = new ProcessBuilder();
            logger.info("Start executing command to cmd.exe ");


            if (isAudio) {
                builder.command(TERMINAL, "/c",
                         "youtube-dl -f bestaudio " +
                                " -o " + folderManagerService.getPath()+ "/" + request.getVideoId() + currentFormat
                                + String.format(YOUTUBE_VIDEO_URL, request.getVideoId()));
            } else {
                builder.command(TERMINAL, "/c",
                        "youtube-dl -f " + request.getQualityCode() +
                                " -o " + folderManagerService.getPath()+ "/"+ request.getVideoId() + currentFormat
                                + String.format(YOUTUBE_VIDEO_URL, request.getVideoId()));
            }

            builder.redirectErrorStream(true);

            process = builder.start();

            logger.info("Run process: " + process.info());

            outStream = process.getInputStream();

            stdOutProcessor = new StreamProcessExtractorService(outBuffer, outStream);

            try {
                stdOutProcessor.join();
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

    public void stopDownloading() {
        stdOutProcessor.stopStream();
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();
    }
}
