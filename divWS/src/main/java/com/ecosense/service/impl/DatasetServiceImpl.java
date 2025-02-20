package com.ecosense.service.impl;

import java.io.IOException;
import java.io.StringWriter;
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
import com.ecosense.dto.output.AuthorODTO;
import com.ecosense.dto.output.ContributorODTO;
import com.ecosense.dto.output.DatasetIdODTO;
import com.ecosense.dto.output.DatasetODTO;
import com.ecosense.dto.output.DatasetsODTO;
import com.ecosense.dto.output.DescriptionODTO;
import com.ecosense.dto.output.FileODTO;
import com.ecosense.dto.output.KeywordODTO;
import com.ecosense.dto.output.LicenseODTO;
import com.ecosense.dto.output.MetadataODTO;
import com.ecosense.dto.output.ProjectODTO;
import com.ecosense.dto.output.PropertyRightODTO;
import com.ecosense.dto.output.SOReferenceODTO;
import com.ecosense.dto.output.ShortNameODTO;
import com.ecosense.dto.output.SiteDetailsODTO;
import com.ecosense.dto.output.SiteODTO;
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
			JsonNode siteReferenceNodeList = metadataNode.get("siteReference");

			for (JsonNode siteReferenceNode : siteReferenceNodeList) {
				try {
					String siteSuffix = siteReferenceNode.get("PID").asText();

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

			// SOReference
			JsonNode soReferenceNode = metadataNode.get("SOReference");
			if (soReferenceNode != null) {
				SOReferenceODTO soReference = new SOReferenceODTO();
				setFieldSafely(soReferenceNode, soReference::setName, JsonNode::asText, "name");
				setFieldSafely(soReferenceNode, soReference::setUrl, JsonNode::asText, "url");
				metadata.setSoReference(soReference);
			}

			// authors
			List<AuthorODTO> authors = new ArrayList<>();
			JsonNode authorNodeList = metadataNode.get("authors");
			if (authorNodeList != null) {
				for (JsonNode authorNode : authorNodeList) {
					AuthorODTO author = new AuthorODTO();
					setFieldSafely(authorNode, author::setEmail, JsonNode::asText, "email");
					setFieldSafely(authorNode, author::setFamilyName, JsonNode::asText, "familyName");
					setFieldSafely(authorNode, author::setFullName, JsonNode::asText, "fullName");
					setFieldSafely(authorNode, author::setGivenName, JsonNode::asText, "givenName");

					authors.add(author);
				}
			}
			metadata.setAuthors(authors);

			// contributors
			List<ContributorODTO> contributors = new ArrayList<>();
			JsonNode contributorNodeList = metadataNode.get("contributors");
			if (contributorNodeList != null) {
				for (JsonNode contributorNode : contributorNodeList) {
					ContributorODTO contributor = new ContributorODTO();
					setFieldSafely(contributorNode, contributor::setEmail, JsonNode::asText, "email");
					setFieldSafely(contributorNode, contributor::setFamilyName, JsonNode::asText, "familyName");
					setFieldSafely(contributorNode, contributor::setFullName, JsonNode::asText, "fullName");
					setFieldSafely(contributorNode, contributor::setGivenName, JsonNode::asText, "givenName");
					setFieldSafely(contributorNode, contributor::setType, JsonNode::asText, "type");

					contributors.add(contributor);
				}
			}
			metadata.setContributors(contributors);

			// Data Level
			setFieldSafely(metadataNode, metadata::setDataLevel, JsonNode::asInt, "dataLevel");

			// Dataset IDs
			List<DatasetIdODTO> datasetIds = new ArrayList<>();
			JsonNode datasetIdNodeList = metadataNode.get("datasetIds");
			if (datasetIdNodeList != null) {
				for (JsonNode datasetIdNode : datasetIdNodeList) {
					DatasetIdODTO datasetId = new DatasetIdODTO();
					setFieldSafely(datasetIdNode, datasetId::setIdentifier, JsonNode::asText, "identifier");
					setFieldSafely(datasetIdNode, datasetId::setSourceName, JsonNode::asText, "sourceName");
					setFieldSafely(datasetIdNode, datasetId::setType, JsonNode::asText, "type");
					setFieldSafely(datasetIdNode, datasetId::setUrl, JsonNode::asText, "url");
					datasetIds.add(datasetId);
				}
			}
			metadata.setDatasetIds(datasetIds);

			// Descriptions
			List<DescriptionODTO> descriptions = new ArrayList<>();
			JsonNode descriptionNodeList = metadataNode.get("descriptions");
			if (descriptionNodeList != null) {
				for (JsonNode descriptionNode : descriptionNodeList) {
					DescriptionODTO description = new DescriptionODTO();
					setFieldSafely(descriptionNode, description::setDescription, JsonNode::asText, "description");
					setFieldSafely(descriptionNode, description::setLanguage, JsonNode::asText, "language");
					setFieldSafely(descriptionNode, description::setType, JsonNode::asText, "type");

					descriptions.add(description);
				}
			}
			metadata.setDescriptions(descriptions);

			// Files
			List<FileODTO> files = new ArrayList<>();
			JsonNode fileNodeList = metadataNode.get("files");
			if (fileNodeList != null) {
				for (JsonNode fileNode : fileNodeList) {
					FileODTO file = new FileODTO();
					setFieldSafely(fileNode, file::setFormat, JsonNode::asText, "format");
					setFieldSafely(fileNode, file::setMd5, JsonNode::asText, "md5");
					setFieldSafely(fileNode, file::setName, JsonNode::asText, "name");
					setFieldSafely(fileNode, file::setSize, JsonNode::asText, "size");
					setFieldSafely(fileNode, file::setSourceUrl, JsonNode::asText, "sourceUrl");

					files.add(file);
				}
			}
			metadata.setFiles(files);

			// Keywords
			List<KeywordODTO> keywords = new ArrayList<>();
			JsonNode keywordNodeList = metadataNode.get("keywords");
			if (keywordNodeList != null) {
				for (JsonNode keywordNode : keywordNodeList) {
					KeywordODTO keyword = new KeywordODTO();
					setFieldSafely(keywordNode, keyword::setName, JsonNode::asText, "name");
					setFieldSafely(keywordNode, keyword::setUrl, JsonNode::asText, "url");

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
					setFieldSafely(licenseNode, license::setName, JsonNode::asText, "name");
					setFieldSafely(licenseNode, license::setUrl, JsonNode::asText, "url");

					licenses.add(license);
				}
			}
			metadata.setLicenses(licenses);

			// Project
			JsonNode projectNode = metadataNode.get("project");
			if (projectNode != null) {
				ProjectODTO project = new ProjectODTO();
				setFieldSafely(projectNode, project::setDOI, JsonNode::asText, "DOI");
				setFieldSafely(projectNode, project::setPID, JsonNode::asText, "PID");
				setFieldSafely(projectNode, project::setName, JsonNode::asText, "name");

				metadata.setProject(project);
			}

			// Property Rights
			List<PropertyRightODTO> propertyRights = new ArrayList<>();
			JsonNode propertyRightNodeList = metadataNode.get("propertyRights");
			if (propertyRightNodeList != null) {
				for (JsonNode propertyRightNode : propertyRightNodeList) {
					PropertyRightODTO propertyRight = new PropertyRightODTO();
					setFieldSafely(propertyRightNode, propertyRight::setName, JsonNode::asText, "name");
					setFieldSafely(propertyRightNode, propertyRight::setUrl, JsonNode::asText, "url");

					propertyRights.add(propertyRight);
				}
			}
			metadata.setPropertyRights(propertyRights);

			// Short Names
			List<ShortNameODTO> shortNames = new ArrayList<>();
			JsonNode shortNameNodeList = metadataNode.get("shortNames");
			if (shortNameNodeList != null) {
				for (JsonNode shortNameNode : shortNameNodeList) {
					ShortNameODTO shortName = new ShortNameODTO();
					setFieldSafely(shortNameNode, shortName::setLanguage, JsonNode::asText, "language");
					setFieldSafely(shortNameNode, shortName::setText, JsonNode::asText, "text");

					shortNames.add(shortName);
				}
			}
			metadata.setShortNames(shortNames);

			// Taxonomic Coverages
			List<TaxonomicCoverageODTO> taxonomicCoverages = new ArrayList<>();
			JsonNode taxonomicCoverageNodeList = metadataNode.get("taxonomicCoverages");
			if (taxonomicCoverageNodeList != null) {
				for (JsonNode taxonomicCoverageNode : taxonomicCoverageNodeList) {
					TaxonomicCoverageODTO taxonomicCoverage = new TaxonomicCoverageODTO();
					setFieldSafely(taxonomicCoverageNode, taxonomicCoverage::setDescription, JsonNode::asText, "description");
					taxonomicCoverages.add(taxonomicCoverage);
				}
			}
			metadata.setTaxonomicCoverages(taxonomicCoverages);


			dataset.setMetadata(metadata);

			JsonNode titles = metadataNode.get("titles");
			if (titles != null) {
				for (JsonNode titleNode : titles) {
					if (titleNode.get("language") != null && titleNode.get("language").asText().equals("eng")) {
						setFieldSafely(titleNode, dataset::setTitle, JsonNode::asText, "text");
						break;
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
