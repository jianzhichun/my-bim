package com.zjjqtech.bimplatform.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class FileUploadArgs implements Serializable {

    private String url;
    private Map<String, String> args;

    public FileUploadArgs(String url, Map<String, String> args) {
        this.url = url;
        this.args = args;
    }

    public FileUploadArgs(String url) {
        this.url = url;
    }

    public FileUploadArgs() {
    }


}
