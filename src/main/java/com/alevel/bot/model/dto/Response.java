package com.alevel.bot.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
public class Response {

    private ResponseContentType contentType;

    private String name;

    private String path;

    public InputStream getContentStream(){
        try {
            return new FileInputStream(path + "/" + name + "." + contentType);

        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
