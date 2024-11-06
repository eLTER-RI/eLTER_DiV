package com.ecosense.dto.output;

import java.io.Serializable;

public class DatasetIdODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String identifier;
    private String sourceName;
    private String type;
    private String url;

    public DatasetIdODTO() { }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
