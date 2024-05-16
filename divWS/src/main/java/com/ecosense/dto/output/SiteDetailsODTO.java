package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SiteDetailsODTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;
	private String description;
	private String activities;
	private String polygon;
	private String title;
	private String siteName;
	private String shortName;
	private String country;
	private String siteType;
	private String keywords;
	private String yearEstablished;
	
	private String experimentsDesign;
	private String experimentsScale;
	private String observationsDesign;
	private String observationsScale;
	
	private Date changed;
	
	private SiteDetailsGeographicODTO geographic;
	private SiteDetailsNameUrlODTO status;
	private SiteDetailsContactODTO contact;
	private SiteDetailnfrastructureODTO infrastructure;
	private SiteDetailsEnvironmentalCharacteristicsODTO environmentalCharacteris;

	private List<SiteDetailsNameUrlODTO> observingCapabilities;
	private List<SiteDetailsNameUrlODTO> belongsTo; 
	private List<SiteDetailsNameUrlODTO> projects;
	private List<SiteDetailsNameUrlODTO> relatedResources;
	private List<SiteDetailsNameUrlODTO> protectionLevel;
	
	public SiteDetailsODTO() {}

	
	public String getExperimentsDesign() {
		return experimentsDesign;
	}

	public void setExperimentsDesign(String experimentsDesign) {
		this.experimentsDesign = experimentsDesign;
	}

	public String getExperimentsScale() {
		return experimentsScale;
	}

	public void setExperimentsScale(String experimentsScale) {
		this.experimentsScale = experimentsScale;
	}

	public String getObservationsDesign() {
		return observationsDesign;
	}

	public void setObservationsDesign(String observationsDesign) {
		this.observationsDesign = observationsDesign;
	}

	public String getObservationsScale() {
		return observationsScale;
	}

	public void setObservationsScale(String observationsScale) {
		this.observationsScale = observationsScale;
	}

	public List<SiteDetailsNameUrlODTO> getProtectionLevel() {
		return protectionLevel;
	}

	public void setProtectionLevel(List<SiteDetailsNameUrlODTO> protectionLevel) {
		this.protectionLevel = protectionLevel;
	}

	public String getYearEstablished() {
		return yearEstablished;
	}

	public void setYearEstablished(String yearEstablished) {
		this.yearEstablished = yearEstablished;
	}

	public SiteDetailsNameUrlODTO getStatus() {
		return status;
	}

	public void setStatus(SiteDetailsNameUrlODTO status) {
		this.status = status;
	}

	public List<SiteDetailsNameUrlODTO> getBelongsTo() {
		return belongsTo;
	}

	public void setBelongsTo(List<SiteDetailsNameUrlODTO> belongsTo) {
		this.belongsTo = belongsTo;
	}

	public List<SiteDetailsNameUrlODTO> getProjects() {
		return projects;
	}

	public void setProjects(List<SiteDetailsNameUrlODTO> projects) {
		this.projects = projects;
	}


	public Date getChanged() {
		return changed;
	}

	public void setChanged(Date changed) {
		this.changed = changed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActivities() {
		return activities;
	}

	public void setActivities(String activities) {
		this.activities = activities;
	}

	public String getPolygon() {
		return polygon;
	}

	public void setPolygon(String polygon) {
		this.polygon = polygon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SiteDetailsNameUrlODTO> getObservingCapabilities() {
		return observingCapabilities;
	}

	public void setObservingCapabilities(List<SiteDetailsNameUrlODTO> observingCapabilities) {
		this.observingCapabilities = observingCapabilities;
	}

	public SiteDetailnfrastructureODTO getInfrastructure() {
		return infrastructure;
	}

	public void setInfrastructure(SiteDetailnfrastructureODTO infrastructure) {
		this.infrastructure = infrastructure;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getSiteType() {
		return siteType;
	}

	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}

	public List<SiteDetailsNameUrlODTO> getRelatedResources() {
		return relatedResources;
	}

	public void setRelatedResources(List<SiteDetailsNameUrlODTO> relatedResources) {
		this.relatedResources = relatedResources;
	}

	public SiteDetailsContactODTO getContact() {
		return contact;
	}

	public void setContact(SiteDetailsContactODTO contact) {
		this.contact = contact;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public SiteDetailsEnvironmentalCharacteristicsODTO getEnvironmentalCharacteris() {
		return environmentalCharacteris;
	}

	public void setEnvironmentalCharacteris(SiteDetailsEnvironmentalCharacteristicsODTO environmentalCharacteris) {
		this.environmentalCharacteris = environmentalCharacteris;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public SiteDetailsGeographicODTO getGeographic() {
		return geographic;
	}

	public void setGeographic(SiteDetailsGeographicODTO geographic) {
		this.geographic = geographic;
	}
	
	
	
}
