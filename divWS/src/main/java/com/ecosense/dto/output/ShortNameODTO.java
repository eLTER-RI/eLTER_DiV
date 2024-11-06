package com.ecosense.dto.output;

import java.io.Serializable;

public class ShortNameODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String language;
    private String text;

    public ShortNameODTO() { }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
