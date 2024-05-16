package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

public class SiteDetailsContactODTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<SiteDetailsNameUrlODTO> siteUrl;
	private List<SiteDetailsContactResponsibleODTO> siteManagers;
	private List<SiteDetailsContactResponsibleODTO> metadataProviders;
	private List<SiteDetailsNameTypeODTO> operatingOrganisation;
	private List<SiteDetailsNameTypeODTO> fundingAgency;
	
	
	public SiteDetailsContactODTO() { }
	
	
	public List<SiteDetailsNameUrlODTO> getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(List<SiteDetailsNameUrlODTO> siteUrl) {
		this.siteUrl = siteUrl;
	}

	public List<SiteDetailsContactResponsibleODTO> getSiteManagers() {
		return siteManagers;
	}
	
	public void setSiteManagers(List<SiteDetailsContactResponsibleODTO> siteManagers) {
		this.siteManagers = siteManagers;
	}
	
	public List<SiteDetailsContactResponsibleODTO> getMetadataProviders() {
		return metadataProviders;
	}
	
	public void setMetadataProviders(List<SiteDetailsContactResponsibleODTO> metadataProviders) {
		this.metadataProviders = metadataProviders;
	}

	public List<SiteDetailsNameTypeODTO> getOperatingOrganisation() {
		return operatingOrganisation;
	}

	public void setOperatingOrganisation(List<SiteDetailsNameTypeODTO> operatingOrganisation) {
		this.operatingOrganisation = operatingOrganisation;
	}

	public List<SiteDetailsNameTypeODTO> getFundingAgency() {
		return fundingAgency;
	}

	public void setFundingAgency(List<SiteDetailsNameTypeODTO> fundingAgency) {
		this.fundingAgency = fundingAgency;
	}
	
	

}

