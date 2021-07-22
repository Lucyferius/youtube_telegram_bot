package com.alevel.bot.service.util;

import com.alevel.bot.exceptions.YoutubeBotExceptions;
import com.alevel.bot.model.dto.Request;
import com.alevel.bot.model.dto.Response;
import com.alevel.bot.model.dto.ResponseContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service()
public class UserResponseProcessor {

    private final YouTubeDownloader youtubeDownloader;

    private final FolderManagerService folderManagerService;

    @Autowired
    public UserResponseProcessor(final YouTubeDownloader youtubeDownloader,
                                 FolderManagerService folderManagerService) {
        this.youtubeDownloader = youtubeDownloader;
        this.folderManagerService = folderManagerService;
    }

    public Response processResponse(Request request) throws YoutubeBotExceptions {
        boolean isAudio = ResponseContentType.mp3.equals(request.getFormat());
        String videoId = request.getVideoId();

        ResponseContentType type;

        youtubeDownloader.download(request);

        type = isAudio
                ? ResponseContentType.mp3
                : ResponseContentType.mp4;

        return new Response(
                type,
                videoId,
                folderManagerService.getPath()
        );
    }

    public void stopDownload() {
        youtubeDownloader.stopDownloading();
    }
}
