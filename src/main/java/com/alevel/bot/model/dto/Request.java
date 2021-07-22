package com.alevel.bot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    private String videoId;

    private ResponseContentType format;

    private int qualityCode;

    private boolean isProcessing = false;

    public String getFullName(){
        return new StringBuilder().append(videoId).append(".").append(format).toString() ;
    }
}
