package com.ecosense.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EbvDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private Integer id;
    private String namingAuthority;
    private String title;
    private String dateCreated;
    private String summary;
    private List<String> references;
    private String source;
    private String coverageContentType;
    private String processingLevel;
    private String project;
    private List<String> projectUrl;
    private EbvCreatorDetailNodeDTO creator;
    private String contributorName;
    private String license;
    private EbvPublisherDetailNodeDTO publisher;
    private EbvDetailNodeDTO ebv;
    private EbvEntityDetailNodeDTO ebvEntity;
    private EbvMetricsDetailNodeDTO ebvMetric;
    private EbvSpatialDetailNodeDTO ebvSpatial;
    private String geospatialLatUnits;
    private String geospatialLonUnits;
    private EbvTimeCoverageDetailNodeDTO timeCoverage;
    private String ebvDomain;
    private String comment;
    private EbvDatasetDetailNodeDTO dataset;
    private EbvFileDetailNodeDTO file;
    
    public EbvDetailDTO() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public String getNamingAuthority() {
		return namingAuthority;
	}

	public String getTitle() {
		return title;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public String getSummary() {
		return summary;
	}

	public List<String> getReferences() {
		return references;
	}

	public String getSource() {
		return source;
	}

	public String getCoverageContentType() {
		return coverageContentType;
	}

	public String getProcessingLevel() {
		return processingLevel;
	}

	public String getProject() {
		return project;
	}

	public List<String> getProjectUrl() {
		return projectUrl;
	}

	public EbvCreatorDetailNodeDTO getCreator() {
		return creator;
	}

	public String getContributorName() {
		return contributorName;
	}

	public String getLicense() {
		return license;
	}

	public EbvPublisherDetailNodeDTO getPublisher() {
		return publisher;
	}

	public EbvDetailNodeDTO getEbv() {
		return ebv;
	}

	public EbvEntityDetailNodeDTO getEbvEntity() {
		return ebvEntity;
	}

	public EbvMetricsDetailNodeDTO getEbvMetric() {
		return ebvMetric;
	}

	public EbvSpatialDetailNodeDTO getEbvSpatial() {
		return ebvSpatial;
	}

	public String getGeospatialLatUnits() {
		return geospatialLatUnits;
	}

	public String getGeospatialLonUnits() {
		return geospatialLonUnits;
	}

	public EbvTimeCoverageDetailNodeDTO getTimeCoverage() {
		return timeCoverage;
	}

	public String getEbvDomain() {
		return ebvDomain;
	}

	public String getComment() {
		return comment;
	}

	public EbvDatasetDetailNodeDTO getDataset() {
		return dataset;
	}

	public EbvFileDetailNodeDTO getFile() {
		return file;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNamingAuthority(String namingAuthority) {
		this.namingAuthority = namingAuthority;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setReferences(List<String> references) {
		this.references = references;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setCoverageContentType(String coverageContentType) {
		this.coverageContentType = coverageContentType;
	}

	public void setProcessingLevel(String processingLevel) {
		this.processingLevel = processingLevel;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public void setProjectUrl(List<String> projectUrl) {
		this.projectUrl = projectUrl;
	}

	public void setCreator(EbvCreatorDetailNodeDTO creator) {
		this.creator = creator;
	}

	public void setContributorName(String contributorName) {
		this.contributorName = contributorName;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public void setPublisher(EbvPublisherDetailNodeDTO publisher) {
		this.publisher = publisher;
	}

	public void setEbv(EbvDetailNodeDTO ebv) {
		this.ebv = ebv;
	}

	public void setEbvEntity(EbvEntityDetailNodeDTO ebvEntity) {
		this.ebvEntity = ebvEntity;
	}

	public void setEbvMetric(EbvMetricsDetailNodeDTO ebvMetric) {
		this.ebvMetric = ebvMetric;
	}

	public void setEbvSpatial(EbvSpatialDetailNodeDTO ebvSpatial) {
		this.ebvSpatial = ebvSpatial;
	}

	public void setGeospatialLatUnits(String geospatialLatUnits) {
		this.geospatialLatUnits = geospatialLatUnits;
	}

	public void setGeospatialLonUnits(String geospatialLonUnits) {
		this.geospatialLonUnits = geospatialLonUnits;
	}

	public void setTimeCoverage(EbvTimeCoverageDetailNodeDTO timeCoverage) {
		this.timeCoverage = timeCoverage;
	}

	public void setEbvDomain(String ebvDomain) {
		this.ebvDomain = ebvDomain;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDataset(EbvDatasetDetailNodeDTO dataset) {
		this.dataset = dataset;
	}

	public void setFile(EbvFileDetailNodeDTO file) {
		this.file = file;
	}
    
    
	

}
