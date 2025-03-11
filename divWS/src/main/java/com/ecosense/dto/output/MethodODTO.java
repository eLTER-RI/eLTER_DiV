package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

public class MethodODTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private String methodID;
    private String qualityControlDescription;
    private String instrumentationDescription;
    
    private SamplingODTO sampling;

    private List<StepODTO> steps;

    public MethodODTO() { }

    public String getMethodID() {
        return methodID;
    }

    public void setMethodID(String methodID) {
        this.methodID = methodID;
    }

    public String getQualityControlDescription() {
        return qualityControlDescription;
    }

    public void setQualityControlDescription(String qualityControlDescription) {
        this.qualityControlDescription = qualityControlDescription;
    }

    public String getInstrumentationDescription() {
        return instrumentationDescription;
    }

    public void setInstrumentationDescription(String instrumentationDescription) {
        this.instrumentationDescription = instrumentationDescription;
    }

    public SamplingODTO getSampling() {
        return sampling;
    }

    public void setSampling(SamplingODTO sampling) {
        this.sampling = sampling;
    }

    public List<StepODTO> getSteps() {
        return steps;
    }

    public void setSteps(List<StepODTO> steps) {
        this.steps = steps;
    }

}
