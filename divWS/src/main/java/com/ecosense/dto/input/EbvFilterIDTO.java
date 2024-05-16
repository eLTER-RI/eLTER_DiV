package com.ecosense.dto.input;

import java.io.Serializable;
import java.util.Date;

import com.ecosense.dto.EbvClassDTO;
import com.ecosense.dto.EbvEntityTypeDTO;
import com.ecosense.dto.EbvNameDTO;
import com.ecosense.dto.EbvSpatialScopeDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EbvFilterIDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String creator;
    private Date date;
    private EbvClassDTO ebvClass;
    private EbvNameDTO ebvName;
    private EbvEntityTypeDTO entityType;
    private EbvSpatialScopeDTO spatialScope;
    
    public EbvFilterIDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getCreator() {
		return creator;
	}

	public EbvClassDTO getEbvClass() {
		return ebvClass;
	}

	public EbvNameDTO getEbvName() {
		return ebvName;
	}

	public EbvEntityTypeDTO getEntityType() {
		return entityType;
	}

	public EbvSpatialScopeDTO getSpatialScope() {
		return spatialScope;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setEbvClass(EbvClassDTO ebvClass) {
		this.ebvClass = ebvClass;
	}

	public void setEbvName(EbvNameDTO ebvName) {
		this.ebvName = ebvName;
	}

	public void setEntityType(EbvEntityTypeDTO entityType) {
		this.entityType = entityType;
	}

	public void setSpatialScope(EbvSpatialScopeDTO spatialScope) {
		this.spatialScope = spatialScope;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
    
    

}
