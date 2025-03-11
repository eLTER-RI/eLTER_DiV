package com.ecosense.dto.output;

import java.io.Serializable;

public class SamplingODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String studyDescription;
    private String samplingDescription;

    public SamplingODTO() { }

    public String getStudyDescription() {
        return studyDescription;
    }

    public void setStudyDescription(String studyDescription) {
        this.studyDescription = studyDescription;
    }

    public String getSamplingDescription() {
        return samplingDescription;
    }

    public void setSamplingDescription(String samplingDescription) {
        this.samplingDescription = samplingDescription;
    }

}
