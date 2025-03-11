package com.ecosense.dto.output;

import java.io.Serializable;

public class StepODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String stepTitle;
    private String stepDescription;

    public StepODTO() { }

    public String getStepTitle() {
        return stepTitle;
    }

    public void setStepTitle(String stepTitle) {
        this.stepTitle = stepTitle;
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

}
