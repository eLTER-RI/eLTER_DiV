package com.ecosense.dto;

import java.io.Serializable;

public class EbvTimeCoverageDetailNodeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private String timeCoverageResolution;
    private String timeCoverageStart;
    private String timeCoverageEnd;
    
	public EbvTimeCoverageDetailNodeDTO(String timeCoverageResolution, String timeCoverageStart, String timeCoverageEnd) {
		super();
		this.timeCoverageResolution = timeCoverageResolution;
		this.timeCoverageStart = timeCoverageStart;
		this.timeCoverageEnd = timeCoverageEnd;
	}

	public String getTimeCoverageResolution() {
		return timeCoverageResolution;
	}

	public String getTimeCoverageStart() {
		return timeCoverageStart;
	}

	public String getTimeCoverageEnd() {
		return timeCoverageEnd;
	}

	public void setTimeCoverageResolution(String timeCoverageResolution) {
		this.timeCoverageResolution = timeCoverageResolution;
	}

	public void setTimeCoverageStart(String timeCoverageStart) {
		this.timeCoverageStart = timeCoverageStart;
	}

	public void setTimeCoverageEnd(String timeCoverageEnd) {
		this.timeCoverageEnd = timeCoverageEnd;
	}
	
	
    
    

}
