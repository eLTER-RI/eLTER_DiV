package com.ecosense.dto.output;

import java.io.Serializable;

public class FileODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String format;
    private String md5;
    private String name;
    private String size;
    private String sourceUrl;

    public FileODTO() { }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
