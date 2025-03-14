package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MetadataODTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer dataLevel;
    private String language;
    private Date publicationDate;

    private List<ProjectODTO> projects;
    private List<CreatorODTO> creators;
    private List<ContributorODTO> contributors;
    private List<DescriptionODTO> descriptions;
    private List<KeywordODTO> keywords;
    private List<LicenseODTO> licenses;
    private List<TaxonomicCoverageODTO> taxonomicCoverages;
    private List<ResponsibleOrganizationODTO> responsibleOrganizations;
    private List<ContactPointODTO> contactPoints;
    private List<MethodODTO> methods;
    private List<HabitatReferenceODTO> habitatReferences;
    private List<AdditionalMetadataODTO> additionalMetadatas;
    private ExternalSourceInformationODTO externalSourceInformation;
    private List<FileODTO> files;


    public MetadataODTO() { }

    public List<CreatorODTO> getCreators() {
        return creators;
    }

    public void setCreators(List<CreatorODTO> creators) {
        this.creators = creators;
    }

    public List<ContributorODTO> getContributors() {
        return contributors;
    }

    public void setContributors(List<ContributorODTO> contributors) {
        this.contributors = contributors;
    }

    public Integer getDataLevel() {
        return dataLevel;
    }

    public void setDataLevel(Integer dataLevel) {
        this.dataLevel = dataLevel;
    }

    public List<DescriptionODTO> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionODTO> descriptions) {
        this.descriptions = descriptions;
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

    public List<ProjectODTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectODTO> projects) {
        this.projects = projects;
    }

    public List<TaxonomicCoverageODTO> getTaxonomicCoverages() {
        return taxonomicCoverages;
    }

    public void setTaxonomicCoverages(List<TaxonomicCoverageODTO> taxonomicCoverages) {
        this.taxonomicCoverages = taxonomicCoverages;
    }

    public List<ResponsibleOrganizationODTO> getResponsibleOrganizations() {
        return responsibleOrganizations;
    }

    public void setResponsibleOrganizations(List<ResponsibleOrganizationODTO> responsibleOrganizations) {
        this.responsibleOrganizations = responsibleOrganizations;
    }

    public List<ContactPointODTO> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(List<ContactPointODTO> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public List<MethodODTO> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodODTO> methods) {
        this.methods = methods;
    }

    public List<HabitatReferenceODTO> getHabitatReferences() {
        return habitatReferences;
    }

    public void setHabitatReferences(List<HabitatReferenceODTO> habitatReferences) {
        this.habitatReferences = habitatReferences;
    }

    public List<AdditionalMetadataODTO> getAdditionalMetadatas() {
        return additionalMetadatas;
    }

    public void setAdditionalMetadatas(List<AdditionalMetadataODTO> additionalMetadatas) {
        this.additionalMetadatas = additionalMetadatas;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public ExternalSourceInformationODTO getExternalSourceInformation() {
        return externalSourceInformation;
    }

    public void setExternalSourceInformation(ExternalSourceInformationODTO externalSourceInformation) {
        this.externalSourceInformation = externalSourceInformation;
    }

    public List<FileODTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileODTO> files) {
        this.files = files;
    }
    
}
