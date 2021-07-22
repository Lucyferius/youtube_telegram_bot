package com.alevel.bot.model.entity;

import com.alevel.bot.model.dto.ResponseContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "uploaded_files",
        uniqueConstraints = {
        @UniqueConstraint(columnNames =
                { "youtube_video_id", "media_type" })})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "youtube_video_id", nullable = false)
    private String youtubeVideoId;

    @Column(name = "telegram_file_id",nullable = false, unique = true )
    private String telegramFileId;

    @Column(name = "media_type",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ResponseContentType type;

    public UploadedFile(String youtubeVideoId, String telegramFileId, ResponseContentType type) {
        this.youtubeVideoId = youtubeVideoId;
        this.telegramFileId = telegramFileId;
        this.type = type;
    }
}
