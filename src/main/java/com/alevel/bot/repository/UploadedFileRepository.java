package com.alevel.bot.repository;

import com.alevel.bot.model.dto.ResponseContentType;
import com.alevel.bot.model.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    UploadedFile findDistinctByYoutubeVideoIdAndType(String youtubeVideoId, ResponseContentType type);

    void deleteUploadedFileByTelegramFileId(String telegramFileId);

    UploadedFile getUploadedFileById(Long id);
}
