package com.ecosense.dto.output;

import java.io.Serializable;

public class ProjectODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String DOI;
    private String PID;
    private String name;

    public ProjectODTO() { }

    public String getDOI() {
        return DOI;
    }

    public void setDOI(String DOI) {
        this.DOI = DOI;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}