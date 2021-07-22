package com.alevel.bot.service.db;

import com.alevel.bot.model.dto.ResponseContentType;
import com.alevel.bot.model.entity.UploadedFile;

import java.util.List;

public interface UploadedFileOperations {

    void createFile(UploadedFile file);

    UploadedFile getFileByVideoIdAndType(String videoId, ResponseContentType type);

    void deleteFile(String telegramFileId);

    UploadedFile getById (Long id);

    List<UploadedFile> findAll();
}
