package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

public class SiteDetailnfrastructureODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Boolean accessibleAllYear;
	private String accessType;
	private Boolean allPartsAccessible;
	private String maintenanceInterval;
	private Boolean permanentPowerSupply;
	private String notes;
	private String collections;
	private Boolean operationPermanent;
	private String operationNotes;
	private String operationSiteVisitInterval;
	
	private SiteDetailsPolicyODTO policy;
	
	public SiteDetailnfrastructureODTO() {
	}
	
	public Boolean getOperationPermanent() {
		return operationPermanent;
	}

	public void setOperationPermanent(Boolean operationPermanent) {
		this.operationPermanent = operationPermanent;
	}

	public String getOperationNotes() {
		return operationNotes;
	}

	public void setOperationNotes(String operationNotes) {
		this.operationNotes = operationNotes;
	}

	public String getOperationSiteVisitInterval() {
		return operationSiteVisitInterval;
	}

	public void setOperationSiteVisitInterval(String operationSiteVisitInterval) {
		this.operationSiteVisitInterval = operationSiteVisitInterval;
	}

	public Boolean getAccessibleAllYear() {
		return accessibleAllYear;
	}

	public void setAccessibleAllYear(Boolean accessibleAllYear) {
		this.accessibleAllYear = accessibleAllYear;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public Boolean getAllPartsAccessible() {
		return allPartsAccessible;
	}

	public void setAllPartsAccessible(Boolean allPartsAccessible) {
		this.allPartsAccessible = allPartsAccessible;
	}

	public String getMaintenanceInterval() {
		return maintenanceInterval;
	}

	public void setMaintenanceInterval(String maintenanceInterval) {
		this.maintenanceInterval = maintenanceInterval;
	}

	public Boolean getPermanentPowerSupply() {
		return permanentPowerSupply;
	}

	public void setPermanentPowerSupply(Boolean permanentPowerSupply) {
		this.permanentPowerSupply = permanentPowerSupply;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getCollections() {
		return collections;
	}

	public void setCollections(String collections) {
		this.collections = collections;
	}

	public SiteDetailsPolicyODTO getPolicy() {
		return policy;
	}

	public void setPolicy(SiteDetailsPolicyODTO policy) {
		this.policy = policy;
	}

	

}
