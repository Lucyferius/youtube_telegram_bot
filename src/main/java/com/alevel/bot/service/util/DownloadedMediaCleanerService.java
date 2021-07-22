package com.alevel.bot.service.util;

import com.alevel.bot.model.dto.Request;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service()
public class DownloadedMediaCleanerService {

    private final static String[] FILE_EXTENSIONS = {"MP3", "mP3", "Mp3", "mp3", "MP4", "mP4", "Mp4", "mp4"};

    private final FolderManagerService folderManagerService;

    @Autowired
    DownloadedMediaCleanerService(FolderManagerService folderManagerService) {
        this.folderManagerService = folderManagerService;
    }

    public void clean(Request request, boolean isItPart) {
        if(isItPart){
            FileUtils.listFiles(new File(folderManagerService.getPath()), FILE_EXTENSIONS, true).stream()
                    .filter(file -> (request.getFullName()+".part").equals(file.getName()+".part"))
                    .forEach(File::delete);
        }else {
            FileUtils.listFiles(new File(folderManagerService.getPath()), FILE_EXTENSIONS, true).stream()
                    .filter(file -> request.getFullName().equals(file.getName()))
                    .forEach(File::delete);
        }
    }
    public double getFileSize(Request request) {
        File file = new File(folderManagerService.getPath() + "\\" + request.getFullName());
        return file.length() / 1024 / 1024;
    }
}
