package com.ecosense.dto.output;

import java.io.Serializable;

public class ExternalSourceInformationODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String externalSourceInfo;
    private String externalSourceName;
    private String externalSourceURI;

    public ExternalSourceInformationODTO() { }

    public String getExternalSourceInfo() {
        return externalSourceInfo;
    }

    public void setExternalSourceInfo(String externalSourceInfo) {
        this.externalSourceInfo = externalSourceInfo;
    }

    public String getExternalSourceName() {
        return externalSourceName;
    }

    public void setExternalSourceName(String externalSourceName) {
        this.externalSourceName = externalSourceName;
    }

    public String getExternalSourceURI() {
        return externalSourceURI;
    }

    public void setExternalSourceURI(String externalSourceURI) {
        this.externalSourceURI = externalSourceURI;
    }

}
