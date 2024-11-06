package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.List;

public class MetadataODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SOReferenceODTO soReference;
    private Integer dataLevel;
    private String language;
    private ProjectODTO project; 

    private List<AuthorODTO> authors;
    private List<ContributorODTO> contributors;
    private List<DatasetIdODTO> datasetIds;
    private List<DescriptionODTO> descriptions;
    private List<FileODTO> files;
    private List<KeywordODTO> keywords;
    private List<LicenseODTO> licenses;
    private List<PropertyRightODTO> propertyRights;
    private List<ShortNameODTO> shortNames;
    private List<TaxonomicCoverageODTO> taxonomicCoverages;

    public MetadataODTO() { }

    public SOReferenceODTO getSoReference() {
        return soReference;
    }

    public void setSoReference(SOReferenceODTO soReference) {
        this.soReference = soReference;
    }

    public List<AuthorODTO> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorODTO> authors) {
        this.authors = authors;
    }

    public List<ContributorODTO> getContributors() {
        return contributors;
    }

    public void setContributors(List<ContributorODTO> contributors) {
        this.contributors = contributors;
    }

    public List<DatasetIdODTO> getDatasetIds() {
        return datasetIds;
    }

    public void setDatasetId(List<DatasetIdODTO> datasetIds) {
        this.datasetIds = datasetIds;
    }

    public Integer getDataLevel() {
        return dataLevel;
    }

    public void setDataLevel(Integer dataLevel) {
        this.dataLevel = dataLevel;
    }

    public void setDatasetIds(List<DatasetIdODTO> datasetIds) {
        this.datasetIds = datasetIds;
    }

    public List<DescriptionODTO> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionODTO> descriptions) {
        this.descriptions = descriptions;
    }

    public List<FileODTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileODTO> files) {
        this.files = files;
    }

    public List<KeywordODTO> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<KeywordODTO> keywords) {
        this.keywords = keywords;
    }

    public List<LicenseODTO> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<LicenseODTO> licenses) {
        this.licenses = licenses;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ProjectODTO getProject() {
        return project;
    }

    public void setProject(ProjectODTO project) {
        this.project = project;
    }

    public List<PropertyRightODTO> getPropertyRights() {
        return propertyRights;
    }

    public void setPropertyRights(List<PropertyRightODTO> propertyRights) {
        this.propertyRights = propertyRights;
    }

    public List<ShortNameODTO> getShortNames() {
        return shortNames;
    }

    public void setShortNames(List<ShortNameODTO> shortNames) {
        this.shortNames = shortNames;
    }

    public List<TaxonomicCoverageODTO> getTaxonomicCoverages() {
        return taxonomicCoverages;
    }

    public void setTaxonomicCoverages(List<TaxonomicCoverageODTO> taxonomicCoverages) {
        this.taxonomicCoverages = taxonomicCoverages;
    }

}
