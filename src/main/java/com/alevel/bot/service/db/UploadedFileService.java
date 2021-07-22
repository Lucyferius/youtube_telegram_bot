package com.alevel.bot.service.db;

import com.alevel.bot.model.dto.ResponseContentType;
import com.alevel.bot.model.entity.UploadedFile;
import com.alevel.bot.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service()
public class UploadedFileService implements UploadedFileOperations {

    private final UploadedFileRepository repository;

    @Autowired
    public UploadedFileService(UploadedFileRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createFile(UploadedFile file) {
        repository.save(file);
    }

    @Override
    public UploadedFile getFileByVideoIdAndType(String videoId, ResponseContentType type) {
        return repository.findDistinctByYoutubeVideoIdAndType(videoId, type);
    }

    @Override
    public void deleteFile(String telegramFileId) {
        repository.deleteUploadedFileByTelegramFileId(telegramFileId);
    }

    @Override
    public UploadedFile getById(Long id){
        return repository.getUploadedFileById(id);
    }

    public List<UploadedFile> findAll(){
        return repository.findAll();
    }
}
