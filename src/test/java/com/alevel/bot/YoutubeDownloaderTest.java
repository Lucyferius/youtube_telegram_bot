package com.alevel.bot;

import com.alevel.bot.exceptions.YoutubeBotExceptions;
import com.alevel.bot.model.dto.Request;
import com.alevel.bot.model.dto.ResponseContentType;
import com.alevel.bot.service.message.UserInputParser;
import com.alevel.bot.service.util.FolderManagerService;
import com.alevel.bot.service.util.YouTubeDownloader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class YoutubeDownloaderTest {

    @Autowired
    private YouTubeDownloader downloader;

    @Autowired
    private UserInputParser parser;

    @Autowired
    FolderManagerService folderManagerService;

    @Test
    public void downloadVideo() {
        String validUrl_v1 = "https://www.youtube.com/watch?v=98kIe3gXqck&ab_channel=sweaterweatherhoe";

        Request request = new Request();
        request.setFormat(ResponseContentType.mp3);

        String videoId = parser.getYouTubeVideoId(validUrl_v1);
        Assertions.assertNotNull(videoId);
        request.setVideoId(videoId);

        Assertions.assertDoesNotThrow(() -> downloader.download(request));

        File file = new File(folderManagerService.getPath(), "98kIe3gXqck.mp3");

        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.delete());
    }

    @Test
    public void downloadInvalidCommand() {
        String validUrl_v1 = "https://www.youtube.com/watch?6366dxFf-Os&ab_channel=ArcticMonkeysVEVO";

        Request request = new Request();
        request.setFormat(ResponseContentType.mp3);

        String videoId = parser.getYouTubeVideoId(validUrl_v1);
        Assertions.assertNotNull(videoId);
        request.setVideoId(videoId);

        Assertions.assertThrows(YoutubeBotExceptions.class, () -> downloader.download(request));

        File file = new File(folderManagerService.getPath(), "6366dxFf-Os.mp3");

        Assertions.assertFalse(file.exists());
    }

    @Test
    public void killProcessAndDeleteFile() {
        String validUrl_v1 = "https://www.youtube.com/watch?v=fdVhRNXI9rA&t=1217s&ab_channel=%D0%9F%D0%A0%D0%98%D0%AF%D0%A2%D0%9D%D0%AB%D0%99%D0%98%D0%9B%D0%AC%D0%94%D0%90%D0%A0";

        Request request = new Request();
        request.setFormat(ResponseContentType.mp4);
        request.setQualityCode(22);

        String videoId = parser.getYouTubeVideoId(validUrl_v1);
        Assertions.assertNotNull(videoId);
        request.setVideoId(videoId);

        Runnable runnable = () -> {
            Assertions.assertThrows(YoutubeBotExceptions.class, () -> downloader.download(request));
            System.out.println("Process of downloading was interrupted. Best result");
            System.out.println("Interrupted: " + LocalDateTime.now());
        };
        Thread t = new Thread(runnable);
        t.start();
        System.out.println("Start: " +LocalDateTime.now());
        try {
            Thread.sleep(6000);
            t.interrupt();
            System.out.println("Test Thread wake up: " + LocalDateTime.now());
            downloader.stopDownloading();
        } catch (InterruptedException e) {
            System.out.println("Downloading stopped");
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File file1 = new File(folderManagerService.getPath(), "fdVhRNXI9rA.mp4.part");
        if(file1.exists())
            Assertions.assertTrue(file1.delete());

    }

}
