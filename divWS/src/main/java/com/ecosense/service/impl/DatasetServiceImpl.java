package com.ecosense.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.BoundingBoxDTO;
import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.PageDTO;
import com.ecosense.dto.PointDTO;
import com.ecosense.dto.PolygonDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.output.CreatorODTO;
import com.ecosense.dto.output.AdditionalMetadataODTO;
import com.ecosense.dto.output.ContactPointODTO;
import com.ecosense.dto.output.ContributorODTO;
import com.ecosense.dto.output.DatasetODTO;
import com.ecosense.dto.output.DatasetsODTO;
import com.ecosense.dto.output.DescriptionODTO;
import com.ecosense.dto.output.HabitatReferenceODTO;
import com.ecosense.dto.output.KeywordODTO;
import com.ecosense.dto.output.LicenseODTO;
import com.ecosense.dto.output.MetadataODTO;
import com.ecosense.dto.output.MethodODTO;
import com.ecosense.dto.output.ProjectODTO;
import com.ecosense.dto.output.ResponsibleOrganizationODTO;
import com.ecosense.dto.output.SamplingODTO;
import com.ecosense.dto.output.SiteDetailsODTO;
import com.ecosense.dto.output.SiteODTO;
import com.ecosense.dto.output.StepODTO;
import com.ecosense.dto.output.TaxonomicClassificationODTO;
import com.ecosense.dto.output.TaxonomicCoverageODTO;
import com.ecosense.entity.Site;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.SiteRepo;
import com.ecosense.service.DatasetService;
import com.ecosense.service.SiteService;
import com.ecosense.utils.InvenioData;
import com.ecosense.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class DatasetServiceImpl implements DatasetService {
    
	@Autowired
	private SiteRepo siteRepo;

	@Autowired
	private SiteService siteService;

	public DatasetsODTO filterAndSearchDataset(DivFilterDTO divFilterDTO) throws SimpleException, IOException {
		String url = InvenioData.BASE_URL;

        url = buildUrlWithParams(divFilterDTO, url);

		if (url == null) {
			DatasetsODTO emptyResult = new DatasetsODTO();
			emptyResult.setDatasets(new ArrayList<>());
			return emptyResult;
		}

		url += InvenioData.PAGING_NUMBER_PARAM + divFilterDTO.getPage() + InvenioData.PAGING_SIZE_PARAM + InvenioData.PAGING_SIZE;
		ResponseEntity<JsonNode> datasetResponse = null;
		JsonNode resultNode = null;

		while (datasetResponse == null) {
			try {
				RestTemplate restTemplate1 = new RestTemplate();
				datasetResponse = restTemplate1.exchange(url, HttpMethod.GET, null, JsonNode.class);
				resultNode = datasetResponse.getBody();
			} catch (Exception e) {
				System.out.println(url + " <- SERVIS KOJI PUKNE");
				e.printStackTrace();
				throw new SimpleException(SimpleResponseDTO.INVENIO_API_ERROR);
			}
		}

		JsonNode hitListNode = resultNode.get("hits").get("hits");

		DatasetsODTO response = extractDatasetsFromResponse(hitListNode);

		PageDTO pageDTO = new PageDTO();
		pageDTO.setCurrentPage(divFilterDTO.getPage());
		pageDTO.setTotalElements(resultNode.get("hits").get("total").asInt());
		pageDTO.setFirst(divFilterDTO.getPage() == 1);
		pageDTO.setLast(divFilterDTO.getPage() == Utils.getTotalPageNum(pageDTO.getTotalElements(), InvenioData.PAGING_SIZE));
		pageDTO.setTotalPages(Utils.getTotalPageNum(pageDTO.getTotalElements(), InvenioData.PAGING_SIZE));
		pageDTO.setSize(InvenioData.PAGING_SIZE);

		JsonNode linkNext = resultNode.get("links").get("next");
		pageDTO.setNextUrl(linkNext != null ? linkNext.asText() : null);

		JsonNode linkPrev = resultNode.get("links").get("prev");
		pageDTO.setPrevUrl(linkPrev != null ? linkPrev.asText() : null);

		response.setPage(pageDTO);

        return response;
	}


    private String buildUrlWithParams(DivFilterDTO divFilterDTO, String url) {
        List<String> searchParameters = new ArrayList<>(); // for full text
        List<String> siteSearchParameters = new ArrayList<>(); // for keyword site

		if (divFilterDTO.getSiteIds() != null && !divFilterDTO.getSiteIds().isEmpty()) {
			for (Integer siteId : divFilterDTO.getSiteIds()) {
				SiteDetailsODTO siteODTO = null;
				try {
					siteODTO = siteService.getSiteInfo(siteId);
				} catch (Exception e) { }
				if (siteODTO != null && siteODTO.getTitle() != null) {
					siteSearchParameters.add(siteODTO.getTitle());
				}; 
			}
		}

        if (divFilterDTO.getSites() != null && !divFilterDTO.getSites().isEmpty()) {
			siteSearchParameters.addAll(divFilterDTO.getSites());
		}

        if (divFilterDTO.getHabitats() != null && !divFilterDTO.getHabitats().isEmpty()) {
			searchParameters.addAll(divFilterDTO.getHabitats());
		}
		if (divFilterDTO.getStandardObservations() != null && !divFilterDTO.getStandardObservations().isEmpty()) {
			searchParameters.addAll(divFilterDTO.getStandardObservations());
		}
		if (divFilterDTO.getSearchText() != null && !divFilterDTO.getSearchText().isEmpty()) {
			searchParameters.addAll(Arrays.asList(divFilterDTO.getSearchText()));
		}

		if (searchParameters.isEmpty() && siteSearchParameters.isEmpty()) {
			return null;
		}

		int countParam = 0;
        if (!searchParameters.isEmpty()) {
			url += InvenioData.FULL_TEXT_SEARCH_PARAM;
            for (String param : searchParameters) {
                if (countParam != 0) {
                    url += "+";
                }
                url += "\"" + param + "\"";
                countParam++;
            }
        }

		int countParamSite = 0;
        for (String siteParam : siteSearchParameters) {
			if (countParamSite != 0 || countParam != 0) {
            	url += "&";
			}
			url += InvenioData.SITE_KEY_WORD_SEARCH_PARAM + siteParam;
			countParamSite++;
        }

		System.out.println("URL " + url);
        return url;

    }

	private DatasetsODTO extractDatasetsFromResponse(JsonNode hitListNode) {
		DatasetsODTO response = new DatasetsODTO();

		Set<DatasetODTO> datasets = new HashSet<>();

		for (JsonNode hitNode : hitListNode) {
			JsonNode metadataNode = hitNode.get("metadata");
			MetadataODTO metadata = new MetadataODTO();

			DatasetODTO dataset = new DatasetODTO();

			setFieldSafely(hitNode, dataset::setId, JsonNode::asText, "id");

			// sites
			Set<SiteODTO> sites = new HashSet<>();
			JsonNode siteReferenceNodeList = metadataNode.get("siteReferences");

			for (JsonNode siteReferenceNode : siteReferenceNodeList) {
				try {
					String siteSuffix = siteReferenceNode.get("siteID").asText();

					Site site = siteRepo.getSite(siteSuffix);
		
					if (site != null) {
		
						SiteODTO siteDTO = new SiteODTO();
						siteDTO.setId(site.getId());
						siteDTO.setIdSuffix(site.getIdSuffix());
						siteDTO.setTitle(site.getTitle());
		
						Point point  = (Point) site.getPoint();
						double lng = point.getCoordinate().x;
						double lat = point.getCoordinate().y;
						siteDTO.setPoint(new PointDTO(lat, lng));
		
						Geometry polygon = site.getPolygon();
						Boolean isPoygon = true;
		
						siteDTO.setArea(polygon.getArea());
						
						StringWriter writer = new StringWriter();
						GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
						geoJsonWriter.write(polygon, writer);
						String polygonJson = writer.toString();
						
						BoundingBoxDTO siteBbox = new BoundingBoxDTO(polygon.getEnvelopeInternal().getMinX(), polygon.getEnvelopeInternal().getMinY(),
								polygon.getEnvelopeInternal().getMaxX(), polygon.getEnvelopeInternal().getMaxY());
						
						siteDTO.setPolygon(new PolygonDTO(polygonJson, siteBbox, isPoygon));
		
						sites.add(siteDTO);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}

			dataset.setSites(Arrays.asList(sites.toArray(SiteODTO[]::new)));

			// creators
			List<CreatorODTO> creators = new ArrayList<>();
			JsonNode creatorNodeList = metadataNode.get("creators");
			if (creatorNodeList != null) {
				for (JsonNode creatorNode : creatorNodeList) {
					CreatorODTO creator = new CreatorODTO();
					setFieldSafely(creatorNode, creator::setEmail, JsonNode::asText, "creatorEmail");
					setFieldSafely(creatorNode, creator::setFamilyName, JsonNode::asText, "creatorFamilyName");
					setFieldSafely(creatorNode, creator::setGivenName, JsonNode::asText, "creatorGivenName");

					creators.add(creator);
				}
			}
			metadata.setCreators(creators);

			// contributors
			List<ContributorODTO> contributors = new ArrayList<>();
			JsonNode contributorNodeList = metadataNode.get("contributors");
			if (contributorNodeList != null) {
				for (JsonNode contributorNode : contributorNodeList) {
					ContributorODTO contributor = new ContributorODTO();
					setFieldSafely(contributorNode, contributor::setEmail, JsonNode::asText, "contributorEmail");
					setFieldSafely(contributorNode, contributor::setFamilyName, JsonNode::asText, "contributorFamilyName");
					setFieldSafely(contributorNode, contributor::setGivenName, JsonNode::asText, "contributorGivenName");
					setFieldSafely(contributorNode, contributor::setContributorType, JsonNode::asText, "contributorType");

					contributors.add(contributor);
				}
			}
			metadata.setContributors(contributors);

			// Data Level
			JsonNode dataLevel = metadataNode.get("dataLevel");
			if (dataLevel != null) {
				setFieldSafely(metadataNode, metadata::setDataLevel, JsonNode::asInt, "dataLevelCode");
			}

			// Descriptions
			List<DescriptionODTO> descriptions = new ArrayList<>();
			JsonNode descriptionNodeList = metadataNode.get("descriptions");
			if (descriptionNodeList != null) {
				for (JsonNode descriptionNode : descriptionNodeList) {
					DescriptionODTO description = new DescriptionODTO();
					setFieldSafely(descriptionNode, description::setDescription, JsonNode::asText, "descriptionText");
					setFieldSafely(descriptionNode, description::setType, JsonNode::asText, "descriptionType");

					descriptions.add(description);
				}
			}
			metadata.setDescriptions(descriptions);

			// Keywords
			List<KeywordODTO> keywords = new ArrayList<>();
			JsonNode keywordNodeList = metadataNode.get("keywords");
			if (keywordNodeList != null) {
				for (JsonNode keywordNode : keywordNodeList) {
					KeywordODTO keyword = new KeywordODTO();
					setFieldSafely(keywordNode, keyword::setName, JsonNode::asText, "keywordLabel");
					setFieldSafely(keywordNode, keyword::setUrl, JsonNode::asText, "keywordURI");

					keywords.add(keyword);
				}
			}
			metadata.setKeywords(keywords);

			// Language
			setFieldSafely(metadataNode, metadata::setLanguage, JsonNode::asText, "language");

			// Licenses
			List<LicenseODTO> licenses = new ArrayList<>();
			JsonNode licenseNodeList = metadataNode.get("licenses");
			if (licenseNodeList != null) {
				for (JsonNode licenseNode : licenseNodeList) {
					LicenseODTO license = new LicenseODTO();
					setFieldSafely(licenseNode, license::setName, JsonNode::asText, "licenseCode");
					setFieldSafely(licenseNode, license::setUrl, JsonNode::asText, "licenseURI");

					licenses.add(license);
				}
			}
			metadata.setLicenses(licenses);

			// Projects
			List<ProjectODTO> projects = new ArrayList<>();
			JsonNode projectNodeList = metadataNode.get("projects");
			if (projectNodeList != null) {
				for (JsonNode projectNode : projectNodeList) {
					ProjectODTO project = new ProjectODTO();
					setFieldSafely(projectNode, project::setPID, JsonNode::asText, "projectID");
					setFieldSafely(projectNode, project::setName, JsonNode::asText, "projectName");

					projects.add(project);
				}
			}
			metadata.setProjects(projects);

			// Taxonomic Coverages
			List<TaxonomicCoverageODTO> taxonomicCoverages = new ArrayList<>();
			JsonNode taxonomicCoverageNodeList = metadataNode.get("taxonomicCoverages");
			if (taxonomicCoverageNodeList != null) {
				for (JsonNode taxonomicCoverageNode : taxonomicCoverageNodeList) {
					TaxonomicCoverageODTO taxonomicCoverage = new TaxonomicCoverageODTO();
					setFieldSafely(taxonomicCoverageNode, taxonomicCoverage::setDescription, JsonNode::asText, "taxonomicDescription");

					JsonNode taxonomicClassificationNode = metadataNode.get("taxonomicClassification");
					TaxonomicClassificationODTO taxonomicClassification = new TaxonomicClassificationODTO();
					setFieldSafely(taxonomicClassificationNode, taxonomicClassification::setTaxonomicClassificationID, JsonNode::asText, "taxonomicClassificationID");
					setFieldSafely(taxonomicClassificationNode, taxonomicClassification::setTaxonomicRankName, JsonNode::asText, "taxonomicRankName");
					setFieldSafely(taxonomicClassificationNode, taxonomicClassification::setTaxonomicRankValue, JsonNode::asText, "taxonomicRankValue");
					setFieldSafely(taxonomicClassificationNode, taxonomicClassification::setTaxonomicCommonName, JsonNode::asText, "taxonomicCommonName");
					taxonomicCoverage.setTaxonomicClassification(taxonomicClassification);

					taxonomicCoverages.add(taxonomicCoverage);
				}
			}
			metadata.setTaxonomicCoverages(taxonomicCoverages);

			// Responsible Organizations
			List<ResponsibleOrganizationODTO> responsibleOrganizations = new ArrayList<>();
			JsonNode responsibleOrganizationNodeList = metadataNode.get("responsibleOrganizations");
			if (responsibleOrganizationNodeList != null) {
				for (JsonNode responsibleOrganizationNode : responsibleOrganizationNodeList) {
					ResponsibleOrganizationODTO reponsibleOrg = new ResponsibleOrganizationODTO();
					setFieldSafely(responsibleOrganizationNode, reponsibleOrg::setOrganizationName, JsonNode::asText, "organizationName");
					setFieldSafely(responsibleOrganizationNode, reponsibleOrg::setOrganizationEmail, JsonNode::asText, "organizationEmail");

					responsibleOrganizations.add(reponsibleOrg);
				}
			}
			metadata.setResponsibleOrganizations(responsibleOrganizations);

			// Contact Points
			List<ContactPointODTO> contactPoints = new ArrayList<>();
			JsonNode contactPointNodeList = metadataNode.get("contactPoints");
			if (contactPointNodeList != null) {
				for (JsonNode contactPointNode : contactPointNodeList) {
					ContactPointODTO contactPoint = new ContactPointODTO();
					setFieldSafely(contactPointNode, contactPoint::setContactName, JsonNode::asText, "contactName");
					setFieldSafely(contactPointNode, contactPoint::setContactEmail, JsonNode::asText, "contactEmail");

					contactPoints.add(contactPoint);
				}
			}
			metadata.setContactPoints(contactPoints);

			// Methods
			List<MethodODTO> methods = new ArrayList<>();
			JsonNode methodNodeList = metadataNode.get("contactPoints");
			if (methodNodeList != null) {
				System.out.println("METHODS");
				for (JsonNode methodNode : methodNodeList) {
					MethodODTO method = new MethodODTO();
					setFieldSafely(methodNode, method::setMethodID, JsonNode::asText, "methodID");
					setFieldSafely(methodNode, method::setQualityControlDescription, JsonNode::asText, "qualityControlDescription");
					setFieldSafely(methodNode, method::setInstrumentationDescription, JsonNode::asText, "instrumentationDescription");

					// Sampling
					JsonNode samplingNode = methodNode.get("sampling");
					if (samplingNode != null) {
						SamplingODTO sampling = new SamplingODTO();
						setFieldSafely(samplingNode, sampling::setStudyDescription, JsonNode::asText, "studyDescription");
						setFieldSafely(samplingNode, sampling::setSamplingDescription, JsonNode::asText, "samplingDescription");

						method.setSampling(sampling);
					}

					// Steps
					List<StepODTO> steps = new ArrayList<>();
					JsonNode stepNodeList = methodNode.get("steps");
					if (stepNodeList != null) {
						for (JsonNode stepNode : stepNodeList) {
							StepODTO step = new StepODTO();
							setFieldSafely(stepNode, step::setStepTitle, JsonNode::asText, "stepTitle");
							setFieldSafely(stepNode, step::setStepDescription, JsonNode::asText, "stepDescription");

							steps.add(step);
						}
					}
					method.setSteps(steps);

					methods.add(method);
				}
			}
			metadata.setMethods(methods);

			// Habitat References
			List<HabitatReferenceODTO> habitatRefs = new ArrayList<>();
			JsonNode habitatRefNodeList = metadataNode.get("habitatReferences");
			if (habitatRefNodeList != null) {
				for (JsonNode habitatRefNode : habitatRefNodeList) {
					HabitatReferenceODTO habitatRef = new HabitatReferenceODTO();
					setFieldSafely(habitatRefNode, habitatRef::setSoHabitatURI, JsonNode::asText, "soHabitatURI");
					setFieldSafely(habitatRefNode, habitatRef::setSoHabitatCode, JsonNode::asText, "soHabitatCode");

					habitatRefs.add(habitatRef);
				}
			}
			metadata.setHabitatReferences(habitatRefs);

			// Additional Metadata
			List<AdditionalMetadataODTO> additionalMetadatas = new ArrayList<>();
			JsonNode additionalMetadataNodeList = metadataNode.get("additionalMetadata");
			if (additionalMetadataNodeList != null) {
				for (JsonNode additionalMetadataNode : additionalMetadataNodeList) {
					AdditionalMetadataODTO additionalMetadata = new AdditionalMetadataODTO();
					setFieldSafely(additionalMetadataNode, additionalMetadata::setName, JsonNode::asText, "name");
					setFieldSafely(additionalMetadataNode, additionalMetadata::setValue, JsonNode::asText, "value");

					additionalMetadatas.add(additionalMetadata);
				}
			}
			metadata.setAdditionalMetadatas(additionalMetadatas);


			dataset.setMetadata(metadata);

			JsonNode titles = metadataNode.get("titles");
			if (titles != null) {
				if (titles.size() == 1) {
					setFieldSafely(titles.get(0), dataset::setTitle, JsonNode::asText, "titleText");
				} else {
					for (JsonNode titleNode : titles) {
						if (titleNode.get("titleLanguage") != null && titleNode.get("titleLanguage").asText().toUpperCase().contains("ENG")) {
							setFieldSafely(titleNode, dataset::setTitle, JsonNode::asText, "titleText");
							break;
						}
					}
				}
			}

			datasets.add(dataset);
		}

		response.setDatasets(Arrays.asList(datasets.toArray(DatasetODTO[]::new)));

		return response;

	}

	private <T> void setFieldSafely(JsonNode node, Consumer<T> setter, Function<JsonNode, T> extractor, String... paths) {
		T value = getNestedValue(node, extractor, paths);
		if (value != null) {
			setter.accept(value);
		}
	}
	
	private <T> T getNestedValue(JsonNode node, Function<JsonNode, T> extractor, String... paths) {
		for (String path : paths) {
			if (node == null || node.isNull()) {
				return null;
			}
			node = node.get(path);
		}
		
		return (node != null && !node.isNull()) ? extractor.apply(node) : null;
	}
    
}
