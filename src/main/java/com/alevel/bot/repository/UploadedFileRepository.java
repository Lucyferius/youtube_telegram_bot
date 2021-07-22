package com.alevel.bot.repository;

import com.alevel.bot.model.dto.ResponseContentType;
import com.alevel.bot.model.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    UploadedFile findDistinctByYoutubeVideoIdAndType(String youtubeVideoId, ResponseContentType type);

    void deleteUploadedFileByTelegramFileId(String telegramFileId);

    UploadedFile getUploadedFileById(Long id);

    @Query("from UploadedFile ")
    @Modifying
    List<UploadedFile> findAll();

}
