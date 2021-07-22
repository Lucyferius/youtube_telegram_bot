package com.alevel.bot.service.util;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FolderManagerService {
    private final String path;

    public FolderManagerService() {
        path = System.getProperty("user.dir") + "/youtubeFiles";
    }

    public void createDirectoryForFiles() {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public String getPath() {
        return path;
    }
}
