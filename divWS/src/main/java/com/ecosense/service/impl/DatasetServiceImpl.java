package com.ecosense.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

			dataset.setId(hitNode.get("id").asText());

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
				soReference.setName(soReferenceNode.get("name").asText());
				soReference.setUrl(soReferenceNode.get("url").asText());
				metadata.setSoReference(soReference);
			}

			// authors
			List<AuthorODTO> authors = new ArrayList<>();
			JsonNode authorNodeList = metadataNode.get("authors");
			if (authorNodeList != null) {
				for (JsonNode authorNode : authorNodeList) {
					AuthorODTO author = new AuthorODTO();
					author.setEmail(authorNode.get("email").asText());
					author.setFamilyName(authorNode.get("familyName").asText());
					author.setFullName(authorNode.get("fullName").asText());
					author.setGivenName(authorNode.get("givenName").asText());

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
					contributor.setEmail(contributorNode.get("email").asText());
					contributor.setFamilyName(contributorNode.get("familyName").asText());
					contributor.setFullName(contributorNode.get("fullName").asText());
					contributor.setGivenName(contributorNode.get("givenName").asText());
					contributor.setType(contributorNode.get("type").asText());

					contributors.add(contributor);
				}
			}
			metadata.setContributors(contributors);

			// Data Level
			metadata.setDataLevel(metadataNode.get("dataLevel").asInt());

			// Dataset IDs
			List<DatasetIdODTO> datasetIds = new ArrayList<>();
			JsonNode datasetIdNodeList = metadataNode.get("datasetIds");
			if (datasetIdNodeList != null) {
				for (JsonNode datasetIdNode : datasetIdNodeList) {
					DatasetIdODTO datasetId = new DatasetIdODTO();
					datasetId.setIdentifier(datasetIdNode.get("identifier").asText());
					datasetId.setSourceName(datasetIdNode.get("sourceName").asText());
					datasetId.setType(datasetIdNode.get("type").asText());
					datasetId.setUrl(datasetIdNode.get("url").asText());
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
					description.setDescription(descriptionNode.get("description").asText());
					description.setLanguage(descriptionNode.get("language").asText());
					description.setType(descriptionNode.get("type").asText());
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
					file.setFormat(fileNode.get("format").asText());
					file.setMd5(fileNode.get("md5").asText());
					file.setName(fileNode.get("name").asText());
					file.setSize(fileNode.get("size").asText());
					file.setSourceUrl(fileNode.get("sourceUrl").asText());
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
					keyword.setName(keywordNode.get("name").asText());
					keyword.setUrl(keywordNode.get("url").asText());
					keywords.add(keyword);
				}
			}
			metadata.setKeywords(keywords);

			// Language
			metadata.setLanguage(metadataNode.get("language").asText());

			// Licenses
			List<LicenseODTO> licenses = new ArrayList<>();
			JsonNode licenseNodeList = metadataNode.get("licenses");
			if (licenseNodeList != null) {
				for (JsonNode licenseNode : licenseNodeList) {
					LicenseODTO license = new LicenseODTO();
					license.setName(licenseNode.get("name").asText());
					license.setUrl(licenseNode.get("url").asText());
					licenses.add(license);
				}
			}
			metadata.setLicenses(licenses);

			// Project
			JsonNode projectNode = metadataNode.get("project");
			if (projectNode != null) {
				ProjectODTO project = new ProjectODTO();
				project.setDOI(projectNode.get("DOI").asText());
				project.setPID(projectNode.get("PID").asText());
				project.setName(projectNode.get("name").asText());
				metadata.setProject(project);
			}

			// Property Rights
			List<PropertyRightODTO> propertyRights = new ArrayList<>();
			JsonNode propertyRightNodeList = metadataNode.get("propertyRights");
			if (propertyRightNodeList != null) {
				for (JsonNode propertyRightNode : propertyRightNodeList) {
					PropertyRightODTO propertyRight = new PropertyRightODTO();
					propertyRight.setName(propertyRightNode.get("name").asText());
					propertyRight.setUrl(propertyRightNode.get("url").asText());
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
					shortName.setLanguage(shortNameNode.get("language").asText());
					shortName.setText(shortNameNode.get("text").asText());
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
					taxonomicCoverage.setDescription(taxonomicCoverageNode.get("description").asText());
					taxonomicCoverages.add(taxonomicCoverage);
				}
			}
			metadata.setTaxonomicCoverages(taxonomicCoverages);


			dataset.setMetadata(metadata);

			JsonNode titles = metadataNode.get("titles");
			if (titles != null) {
				for (JsonNode title : titles) {
					if (title.get("language").equals("eng")) {
						dataset.setTitle(title.get("text").asText());
						break;
					}
				}
			}

			datasets.add(dataset);
		}

		response.setDatasets(Arrays.asList(datasets.toArray(DatasetODTO[]::new)));

		return response;

	}
    
}
