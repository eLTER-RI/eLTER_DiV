package com.ecosense.dto.output;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
    
}
