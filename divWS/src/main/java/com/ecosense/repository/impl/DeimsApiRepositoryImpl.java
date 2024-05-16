package com.ecosense.repository.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.output.SiteDetailnfrastructureODTO;
import com.ecosense.dto.output.SiteDetailsContactODTO;
import com.ecosense.dto.output.SiteDetailsContactResponsibleODTO;
import com.ecosense.dto.output.SiteDetailsEnvironmentalCharacteristicsODTO;
import com.ecosense.dto.output.SiteDetailsGeographicODTO;
import com.ecosense.dto.output.SiteDetailsNameTypeODTO;
import com.ecosense.dto.output.SiteDetailsODTO;
import com.ecosense.dto.output.SiteDetailsPolicyODTO;
import com.ecosense.dto.output.SiteDetailsNameUrlODTO;
import com.ecosense.entity.Activity;
import com.ecosense.entity.Country;
import com.ecosense.entity.Site;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.DeimsApiRepository;
import com.ecosense.utils.DEIMSData;
import com.fasterxml.jackson.databind.JsonNode;

@Repository
public class DeimsApiRepositoryImpl implements DeimsApiRepository {
	

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public List<Site> getAllSites() throws SimpleException {
		List<Site>  sites = new ArrayList<>();
		
		ResponseEntity<JsonNode> sitesResponse = restTemplate.exchange(
				DEIMSData.API_SITE_SEARCH + DEIMSData.NETWORK + DEIMSData.SUFFIX_ID_FOR_LTER_EUROPE + DEIMSData.VERIFIED, 
				HttpMethod.GET, null, JsonNode.class);
		
	    JsonNode sitesNode = sitesResponse.getBody();
	    
	    for(JsonNode oneSiteNode : sitesNode) { 
	    	Site site = new Site();
	     	JsonNode id = oneSiteNode.get("id");
	     	
	    	site.setIdSuffix(id.get("suffix").asText());
	    	site.setTitle(oneSiteNode.get("title").asText());
	    	String changedFromApi = oneSiteNode.get("changed").asText();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'SSS");
	    	try {
				Date changed = sdf.parse(changedFromApi);
				site.setChanged(changed);
			} catch (java.text.ParseException e1) {
				throw new SimpleException(SimpleResponseDTO.PARSE_EXCEPTION);
			}

	    	String point;
			point = oneSiteNode.get("coordinates").asText();
			
			if (point.contains("MULTIPOINT")) {
				continue;
			}
			WKTReader reader = new WKTReader();
			Point geom = null;
			try {
				geom = (Point) reader.read(point);
			} catch (ParseException e) {
				throw new SimpleException(SimpleResponseDTO.PARSE_EXCEPTION);
			}
			site.setPoint(geom);
			
	    	sites.add(site);
	    }
		
		
		return sites;
	}

	@Override
	public SiteDetailsODTO getSiteInfo(String siteIdSuffix) throws SimpleException, SQLException, IOException {
		SiteDetailsODTO siteInfoDTO = new SiteDetailsODTO();
		
		ResponseEntity<JsonNode> siteResponse = restTemplate.exchange(DEIMSData.API_SITE_SEARCH + "/" + siteIdSuffix, 
				HttpMethod.GET, null, JsonNode.class);

	    JsonNode siteNode = siteResponse.getBody();
	    siteInfoDTO.setTitle(siteNode.get("title").asText());
	    
	    String changedFromApi = siteNode.get("changed").asText();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'SSS");
    	try {
			Date changed = sdf.parse(changedFromApi);
			siteInfoDTO.setChanged(changed);
		} catch (java.text.ParseException e1) {
			throw new SimpleException(SimpleResponseDTO.PARSE_EXCEPTION);
		}

	    
	    JsonNode attributesNode = siteNode.get("attributes");
	    JsonNode geographicNode = attributesNode.get("geographic");
	    
	    JsonNode conutriesNode = geographicNode.get("country");
	    if (!conutriesNode.isNull() && conutriesNode.isArray()) {
	    	StringBuilder conutrySB = new StringBuilder();
	    	Iterator<JsonNode> conutriesNodeIterator = conutriesNode.iterator();
	   		while (conutriesNodeIterator.hasNext()) {
	   			JsonNode conutryNode = conutriesNodeIterator.next();
				if (conutriesNodeIterator.hasNext()) {
					conutrySB.append(conutryNode.asText() + "; ");
				} else {
					conutrySB.append(conutryNode.asText());				
				}
    		}
	   		siteInfoDTO.setCountry(conutrySB.toString());
	    }
	    
	    String boundaries = geographicNode.get("boundaries").asText();
	    String geoJson = "";
	    if (boundaries != null && !boundaries.equals("null")) {
	    	WKTReader reader = new WKTReader();
	    	Geometry geom = null;
	    	try {
	    		geom = reader.read(boundaries);
	    		StringWriter writer = new StringWriter();
				GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
				geoJsonWriter.write(geom, writer);
				geoJson = writer.toString();
	    	} catch (ParseException pe) {
	    		pe.printStackTrace();
	    		throw new SimpleException(SimpleResponseDTO.GENERAL_SERVER_ERROR);
	    	}
	    }
	    
	    SiteDetailsGeographicODTO geographic = new SiteDetailsGeographicODTO();
	    JsonNode elevationNode = geographicNode.get("elevation");
	    if (!elevationNode.isNull()) {
	    	geographic.setElevationAvg(elevationNode.get("avg").isNull() ? null : elevationNode.get("avg").asDouble());
	    	geographic.setElevationMin(elevationNode.get("min").isNull() ? null : elevationNode.get("min").asDouble());
	    	geographic.setElevationMax(elevationNode.get("max").isNull() ? null : elevationNode.get("max").asDouble());
	    	geographic.setElevationUnit(checkIsNullTextNode(elevationNode.get("unit")) ? null : elevationNode.get("unit").asText());
	    }
	    
	    JsonNode sizeGeographicNode = geographicNode.get("size");
	    if (!sizeGeographicNode.isNull()) {
	    	geographic.setSizeValue(sizeGeographicNode.get("value").isNull() ? null : sizeGeographicNode.get("value").asDouble());
	    	geographic.setSizeUnit(checkIsNullTextNode(sizeGeographicNode.get("unit")) ? null : sizeGeographicNode.get("unit").asText());
	    }
	    
	    JsonNode environmentalCharacteristicsNode = attributesNode.get("environmentalCharacteristics");
	    SiteDetailsEnvironmentalCharacteristicsODTO environmentalCharacteristics = null;
	    if (!environmentalCharacteristicsNode.isNull()) {
	    	environmentalCharacteristics = new SiteDetailsEnvironmentalCharacteristicsODTO();
	    	
	    	JsonNode airTemperatureNode = environmentalCharacteristicsNode.get("airTemperature");
	    	if (!airTemperatureNode.isNull()) {
	    		environmentalCharacteristics.setAirTemperatureAvgMonthly(checkIsNullTextNode(airTemperatureNode.get("monthlyAverage")) ? null : airTemperatureNode.get("monthlyAverage").asDouble());
	    		environmentalCharacteristics.setAirTemperatureAvg(airTemperatureNode.get("yearlyAverage").asDouble());
	    		environmentalCharacteristics.setAirTemperatureUnit(checkIsNullTextNode(airTemperatureNode.get("unit")) ? null : airTemperatureNode.get("unit").asText());
	    	}
	    	
	    	JsonNode precipitationNode = environmentalCharacteristicsNode.get("precipitation");
	    	if (!precipitationNode.isNull()) {
	    		environmentalCharacteristics.setPrecipitationAvgMonthly(checkIsNullTextNode(precipitationNode.get("monthlyAverage")) ? null : precipitationNode.get("monthlyAverage").asDouble());
	    		environmentalCharacteristics.setPrecipitationAnnual(precipitationNode.get("yearlyAverage").asDouble());
	    		environmentalCharacteristics.setPrecipitationUnit(checkIsNullTextNode(precipitationNode.get("unit")) ? null : precipitationNode.get("unit").asText());
	    	}
	    	
	    	environmentalCharacteristics.setBiome(checkIsNullTextNode(environmentalCharacteristicsNode.get("biome")) ? null : environmentalCharacteristicsNode.get("biome").asText());
	    	environmentalCharacteristics.setBiogeographicalRegion(checkIsNullTextNode(environmentalCharacteristicsNode.get("biogeographicalRegion")) ? null : environmentalCharacteristicsNode.get("biogeographicalRegion").asText());
	    	
	    	JsonNode ecosystemAndLandusesNode = environmentalCharacteristicsNode.get("ecosystemType");
	    	if (!ecosystemAndLandusesNode.isNull() && ecosystemAndLandusesNode.isArray()) {
	    		StringBuilder ecosystemAndLanduseSB = new StringBuilder();
	    		 for (JsonNode ecosystemAndLanduseNode : ecosystemAndLandusesNode) {
	    			 ecosystemAndLanduseSB.append(ecosystemAndLanduseNode.get("label").asText() + "; ");
	    			 ecosystemAndLanduseSB.substring(0, ecosystemAndLanduseSB.length() - 2);
		    	 }
	    		 environmentalCharacteristics.setEcosystemAndLanduse(ecosystemAndLanduseSB.toString());
	    	}
	    	
	    	JsonNode eunisHabitatsNode = environmentalCharacteristicsNode.get("eunisHabitat");
	    	if (!eunisHabitatsNode.isNull() && eunisHabitatsNode.isArray()) {
	    		StringBuilder eunisHabitatSB = new StringBuilder();
	    		 for (JsonNode eunisHabitatNode : eunisHabitatsNode) {
	    			 eunisHabitatSB.append(eunisHabitatNode.get("label").asText() + "; ");
	    			 eunisHabitatSB.substring(0, eunisHabitatSB.length() - 2);
		    	 }
	    		 environmentalCharacteristics.setEunisHabitat(eunisHabitatSB.toString());
	    	}
	    	
	    	

	    	environmentalCharacteristics.setLandforms(checkIsNullTextNode(environmentalCharacteristicsNode.get("landforms")) ? 
	    			null : environmentalCharacteristicsNode.get("landforms").asText());
	    	
	    	environmentalCharacteristics.setGeoBonBiome(checkIsNullTextNode(environmentalCharacteristicsNode.get("geoBonBiome")) ? 
	    			null : environmentalCharacteristicsNode.get("geoBonBiome").asText());
	    	
	    	environmentalCharacteristics.setGeology(checkIsNullTextNode(environmentalCharacteristicsNode.get("geology")) ?
	    			null : environmentalCharacteristicsNode.get("geology").asText());
	    	
	    	environmentalCharacteristics.setHydrology(checkIsNullTextNode(environmentalCharacteristicsNode.get("hydrology")) ? 
	    			null : environmentalCharacteristicsNode.get("hydrology").asText());
	    	
	    	environmentalCharacteristics.setSoils(checkIsNullTextNode(environmentalCharacteristicsNode.get("soils")) ? 
	    			null : environmentalCharacteristicsNode.get("soils").asText());
	    	
	    	environmentalCharacteristics.setVegetation(checkIsNullTextNode(environmentalCharacteristicsNode.get("vegetation")) ?
	    			null : environmentalCharacteristicsNode.get("vegetation").asText());
	    }
	    
	    JsonNode infrastructureNode = attributesNode.get("infrastructure");
    	SiteDetailnfrastructureODTO infrastructure = null;
	    if (!infrastructureNode.isNull()) {
	    	infrastructure = new SiteDetailnfrastructureODTO();
	    	infrastructure.setAccessibleAllYear(infrastructureNode.get("accessibleAllYear").asBoolean());
	    	infrastructure.setAccessType(checkIsNullTextNode(infrastructureNode.get("accessType")) ? null : infrastructureNode.get("accessType").asText());
	    	infrastructure.setAllPartsAccessible(infrastructureNode.get("allPartsAccessible").asBoolean());
	    	infrastructure.setMaintenanceInterval(checkIsNullTextNode(infrastructureNode.get("maintenanceInterval")) ? null : infrastructureNode.get("maintenanceInterval").asText());
	    	infrastructure.setPermanentPowerSupply(infrastructureNode.get("permanentPowerSupply").asBoolean());
	    	infrastructure.setNotes(checkIsNullTextNode(infrastructureNode.get("notes")) ? null : infrastructureNode.get("notes").asText());
	    	
	    	JsonNode collectionsNode = infrastructureNode.get("collection");
	    	if (!collectionsNode.isNull() && collectionsNode.isArray()) {
	    		StringBuilder collectionsSB = new StringBuilder();
	    		 for (JsonNode collectionNode : collectionsNode) {
	    			 collectionsSB.append(collectionNode.get("label").asText() + "; ");
	    			 collectionsSB.substring(0, collectionsSB.length() - 2);
		    	 }
	    		 infrastructure.setCollections(collectionsSB.toString());
	    	}
	    	
	    	if (!infrastructureNode.get("data").isNull()) {
		    	JsonNode policyNode = infrastructureNode.get("data").get("policy");
		    	if (!policyNode.isNull()) {
		    		SiteDetailsPolicyODTO policy = new SiteDetailsPolicyODTO();
		    		
		    		JsonNode rightsNode = policyNode.get("rights");
		    		if (!rightsNode.isNull() && rightsNode.isArray()) {
		    			StringBuilder rightsSB = new StringBuilder();
		    			Iterator<JsonNode> rightsIterator = rightsNode.iterator();
		    			while (rightsIterator.hasNext()) {
		    				JsonNode oneRightsNode = rightsIterator.next();
		    				if (rightsIterator.hasNext()) {
		    					 rightsSB.append(oneRightsNode.asText() + "; ");
		    				} else {
		    					 rightsSB.append(oneRightsNode.asText());
		    				}
		    			}
		    			policy.setRights(rightsSB.toString());
		    		}
		    		policy.setUrl(checkIsNullTextNode(policyNode.get("url")) ? null : policyNode.get("url").asText());
		    		policy.setNotes(checkIsNullTextNode(policyNode.get("notes")) ? null : policyNode.get("notes").asText());
		    		
		    		infrastructure.setPolicy(policy);
		    	}
	    	}
	    	
	    	JsonNode operationNode = infrastructureNode.get("operation");
	    	if (!operationNode.isNull()) {
	    		infrastructure.setOperationNotes(checkIsNullTextNode(operationNode.get("notes")) ? null : operationNode.get("notes").asText());
	    		infrastructure.setOperationPermanent(operationNode.get("permanent").isBoolean() ? operationNode.get("permanent").asBoolean() : null);
	    		infrastructure.setOperationSiteVisitInterval(checkIsNullTextNode(operationNode.get("siteVisitInterval")) ? null : operationNode.get("siteVisitInterval").asText());
	    	}
	    }
	    
	    JsonNode relatedResourcesNode = attributesNode.get("relatedResources");
	    List<SiteDetailsNameUrlODTO> relatedResources = null;
	    if (!relatedResourcesNode.isNull() && relatedResourcesNode.isArray()) {
	    	relatedResources = new ArrayList<>();
	    	for(JsonNode relatedResourceNode : relatedResourcesNode) {
	    		SiteDetailsNameUrlODTO relatedResource = new SiteDetailsNameUrlODTO();
	    		relatedResource.setUrl(relatedResourceNode.get("id").get("prefix").asText() + relatedResourceNode.get("id").get("suffix").asText());
	    		relatedResource.setName(checkIsNullTextNode(relatedResourceNode.get("title")) ? null : relatedResourceNode.get("title").asText());
	    		relatedResources.add(relatedResource);
	    	}
	    }
	    
	    JsonNode contactNode = attributesNode.get("contact");
	    SiteDetailsContactODTO contact = null;
	    if (!contactNode.isNull()) {
	    	contact = new SiteDetailsContactODTO();
	    	
	    	JsonNode siteMaganerNode = contactNode.get("siteManager");
	    	if (!siteMaganerNode.isNull() && siteMaganerNode.isArray()) {
	    		List<SiteDetailsContactResponsibleODTO> siteManages = new ArrayList<>();
	    		for(JsonNode siteManager : siteMaganerNode) {
	    			SiteDetailsContactResponsibleODTO siteManagerObj = new SiteDetailsContactResponsibleODTO();
	    			siteManagerObj.setName(checkIsNullTextNode(siteManager.get("name")) ? null : siteManager.get("name").asText());
	    			siteManagerObj.setEmail(checkIsNullTextNode(siteManager.get("email")) ? null : siteManager.get("email").asText());
	    			siteManagerObj.setOrcid(checkIsNullTextNode(siteManager.get("orcid")) ? null : siteManager.get("orcid").asText());
	    			siteManagerObj.setType(checkIsNullTextNode(siteManager.get("type")) ? null : siteManager.get("type").asText());
	    			siteManages.add(siteManagerObj);
	    		}
	    		contact.setSiteManagers(siteManages);
	    	}
	    	
	    	JsonNode metadataProviderNode = contactNode.get("metadataProvider");
	    	if (!metadataProviderNode.isNull() && metadataProviderNode.isArray()) {
	    		List<SiteDetailsContactResponsibleODTO> metadataProviders = new ArrayList<>();
	    		for(JsonNode metadataProvider : metadataProviderNode) {
	    			SiteDetailsContactResponsibleODTO metadataProviderObj = new SiteDetailsContactResponsibleODTO();
	    			metadataProviderObj.setName(checkIsNullTextNode(metadataProvider.get("name")) ? null : metadataProvider.get("name").asText());
	    			metadataProviderObj.setEmail(checkIsNullTextNode(metadataProvider.get("email"))  ? null : metadataProvider.get("email").asText());
	    			metadataProviderObj.setOrcid(checkIsNullTextNode(metadataProvider.get("orcid"))  ? null : metadataProvider.get("orcid").asText());
	    			metadataProviderObj.setType(checkIsNullTextNode(metadataProvider.get("type"))  ? null : metadataProvider.get("type").asText());
	    			metadataProviders.add(metadataProviderObj);
	    		}
	    		contact.setMetadataProviders(metadataProviders);
	    	}
	    	
	    	JsonNode operatingOrganisationsNode = contactNode.get("operatingOrganisation");
	    	if (!operatingOrganisationsNode.isNull() && operatingOrganisationsNode.isArray()) {
	    		List<SiteDetailsNameTypeODTO> operatingOrganisations = new ArrayList<>();
	    		for(JsonNode operatingOrganisationNode : operatingOrganisationsNode) {
	    			SiteDetailsNameTypeODTO operatingOrganisation = new SiteDetailsNameTypeODTO();
	    			operatingOrganisation.setName(checkIsNullTextNode(operatingOrganisationNode.get("name")) ? null : operatingOrganisationNode.get("name").asText());
	    			operatingOrganisation.setType(checkIsNullTextNode(operatingOrganisationNode.get("type")) ? null : operatingOrganisationNode.get("type").asText());
	    			operatingOrganisations.add(operatingOrganisation);
	    		}
	    		contact.setOperatingOrganisation(operatingOrganisations);
	    	}
	    	
	    	JsonNode fundingAgenciesNode = contactNode.get("fundingAgency");
	    	if (!fundingAgenciesNode.isNull() && fundingAgenciesNode.isArray()) {
	    		List<SiteDetailsNameTypeODTO> fundingAgencies = new ArrayList<>();
	    		for(JsonNode fundingAgencyNode : fundingAgenciesNode) {
	    			SiteDetailsNameTypeODTO fundingAgency = new SiteDetailsNameTypeODTO();
	    			fundingAgency.setName(checkIsNullTextNode(fundingAgencyNode.get("name")) ? null : fundingAgencyNode.get("name").asText());
	    			fundingAgency.setType(checkIsNullTextNode(fundingAgencyNode.get("type")) ? null : fundingAgencyNode.get("type").asText());
	    			fundingAgencies.add(fundingAgency);
	    		}
	    		contact.setFundingAgency(fundingAgencies);
	    	}
	    	
	    	JsonNode sitesUrlNode = contactNode.get("siteUrl");
	    	if (!sitesUrlNode.isNull() && sitesUrlNode.isArray()) {
	    		List<SiteDetailsNameUrlODTO> sitesUrl = new ArrayList<>();
	    		for(JsonNode siteUrlNode : sitesUrlNode) {
	    			SiteDetailsNameUrlODTO siteUrl = new SiteDetailsNameUrlODTO();
	    			siteUrl.setName(checkIsNullTextNode(siteUrlNode.get("title")) ? null : siteUrlNode.get("title").asText());
	    			siteUrl.setUrl(checkIsNullTextNode(siteUrlNode.get("value")) ? null : siteUrlNode.get("value").asText());
	    			sitesUrl.add(siteUrl);
	    		}
	    		contact.setSiteUrl(sitesUrl);
	    	}
	    }
	    
	    JsonNode generalNode = attributesNode.get("general");
	    
	    String description = generalNode.get("abstract").asText();
	    String shortName = generalNode.get("shortName").asText();
	    String siteType = generalNode.get("siteType").asText();
	    String siteName = generalNode.get("siteName").asText();
	    
	    siteInfoDTO.setYearEstablished(checkIsNullTextNode(generalNode.get("yearEstablished")) ? null : generalNode.get("yearEstablished").asText());
	    
	    JsonNode protectionLevelsNode = generalNode.get("protectionLevel");
	    List<SiteDetailsNameUrlODTO> protectionLevels = null;
	    if (!protectionLevelsNode.isNull() && protectionLevelsNode.isArray()) {
	    	protectionLevels = new ArrayList<>();
    		for(JsonNode protectionLevelNode : protectionLevelsNode) {
    			SiteDetailsNameUrlODTO protectionLevel = new SiteDetailsNameUrlODTO();
    			protectionLevel.setName(checkIsNullTextNode(protectionLevelNode.get("label")) ? null : protectionLevelNode.get("label").asText());
    			protectionLevel.setUrl(checkIsNullTextNode(protectionLevelNode.get("uri")) ? null : protectionLevelNode.get("uri").asText());
    			protectionLevels.add(protectionLevel);
    		}
    	}
	    
	    JsonNode keywordsNode = generalNode.get("keywords");
	    StringBuilder keywordsSB = null;
	    if (keywordsNode != null && !keywordsNode.isNull() && keywordsNode.isArray()) {
	    	keywordsSB = new StringBuilder("");
	    	 for (JsonNode keywordNode : keywordsNode) {
	    		 keywordsSB.append(keywordNode.get("label").asText() + "; ");
	    		 keywordsSB.substring(0, keywordsSB.length() - 2);
	    	 }
	    }
	    
	    JsonNode statusNode = generalNode.get("status");
	    SiteDetailsNameUrlODTO status = null;
	    if (!statusNode.isNull()) {
	    	status = new SiteDetailsNameUrlODTO();
	    	status.setName(checkIsNullTextNode(statusNode.get("label")) ? null : statusNode.get("label").asText());
	    	status.setUrl(checkIsNullTextNode(statusNode.get("uri")) ? null : statusNode.get("uri").asText());
	    	
	    }
	    
	    JsonNode focusDesignScaleNode = attributesNode.get("focusDesignScale");

    	JsonNode experimentsNode = focusDesignScaleNode.get("experiments");
	    if (!experimentsNode.isNull()) {
	    	siteInfoDTO.setExperimentsDesign(checkIsNullTextNode(experimentsNode.get("design")) ? null :  experimentsNode.get("design").asText());
	    	siteInfoDTO.setExperimentsScale(checkIsNullTextNode(experimentsNode.get("scale")) ? null :  experimentsNode.get("scale").asText());
	    }
	    
	    JsonNode observationsNode = focusDesignScaleNode.get("observations");
	    if (!observationsNode.isNull()) {
	    	siteInfoDTO.setObservationsDesign(checkIsNullTextNode(observationsNode.get("design")) ? null :  observationsNode.get("design").asText());
	    	siteInfoDTO.setObservationsScale(checkIsNullTextNode(observationsNode.get("scale")) ? null :  observationsNode.get("scale").asText());
	    }
	    
	    JsonNode researchTopicsNode = focusDesignScaleNode.get("researchTopics");
	    
	    StringBuilder activities = new StringBuilder("");
	    
	    if (researchTopicsNode != null && researchTopicsNode.isArray()) {
	    	for (JsonNode elemNode : researchTopicsNode) {
	    		activities.append(elemNode.get("label").asText() + "; ");
	    	}
	    	activities.substring(0, activities.length() - 2);
	    }
	    
	    JsonNode parametarsNode = focusDesignScaleNode.get("observedProperties");
	    List<SiteDetailsNameUrlODTO> parametars = null;
	    
	    if (!parametarsNode.isNull() && parametarsNode.isArray()) {
	    	parametars = new ArrayList<>();
	    	for (JsonNode paramNode : parametarsNode) {
	    		SiteDetailsNameUrlODTO parametar = new SiteDetailsNameUrlODTO();
	    		parametar.setName(checkIsNullTextNode(paramNode.get("label")) ? null : paramNode.get("label").asText());
	    		parametar.setUrl(checkIsNullTextNode(paramNode.get("uri")) ? null : paramNode.get("uri").asText());
	    		parametars.add(parametar);
	    	}
	    }
	    
	    JsonNode networksNode = attributesNode.get("affiliation").get("networks");
	    List<SiteDetailsNameUrlODTO> belongsTo = new ArrayList<>();
	    
	    if (networksNode.isArray()) {
	    	for (JsonNode networkNode : networksNode) {
	    		String url = networkNode.get("network").get("id").get("prefix").asText() + networkNode.get("network").get("id").get("suffix").asText();
	    		belongsTo.add(new SiteDetailsNameUrlODTO(url, networkNode.get("network").get("name").asText()));
			}
	    }
	    
	    JsonNode projectsNode = attributesNode.get("affiliation").get("projects");
	    List<SiteDetailsNameUrlODTO> projects = new ArrayList<>();
	    
	    if (projectsNode.isArray()) {
	    	for (JsonNode projectNode : projectsNode) {
	    		if (projectNode.isNull()) {
	    			 break;
	    		}
	    		SiteDetailsNameUrlODTO siteProjectODTO = new SiteDetailsNameUrlODTO(checkIsNullTextNode(projectNode.get("uri")) ? null : projectNode.get("uri").asText(), 
	    				checkIsNullTextNode(projectNode.get("label")) ? null : projectNode.get("label").asText());
	    		projects.add(siteProjectODTO);
			}
	    }
	    
	    siteInfoDTO.setGeographic(geographic);
	    siteInfoDTO.setProtectionLevel(protectionLevels);
	    siteInfoDTO.setStatus(status);
	    siteInfoDTO.setEnvironmentalCharacteris(environmentalCharacteristics);
	    siteInfoDTO.setRelatedResources(relatedResources);
	    siteInfoDTO.setInfrastructure(infrastructure);
	    siteInfoDTO.setBelongsTo(belongsTo.size() > 0 ? belongsTo : null);
	    siteInfoDTO.setId(siteIdSuffix);
	    siteInfoDTO.setShortName(shortName);
	    siteInfoDTO.setSiteName(siteName);
	    siteInfoDTO.setSiteType(siteType);
	    siteInfoDTO.setPolygon(geoJson);
	    siteInfoDTO.setDescription(description);
	    siteInfoDTO.setActivities(activities.toString());
	    siteInfoDTO.setObservingCapabilities(parametars);
	    siteInfoDTO.setKeywords(keywordsSB == null ? null : keywordsSB.toString());
	    siteInfoDTO.setContact(contact);
	    siteInfoDTO.setProjects(projects.size() > 0 ? projects : null);
		return siteInfoDTO;
	}
	
	private Boolean checkIsNullTextNode(JsonNode node) {
		if (node == null || node.isNull() || node.asText().equals("null")) {
			return true;
		}
		return false;
	}

	@Override
	public Site getPolygon(String siteIdSuffix) throws SimpleException {
		Site site = new Site();
		ResponseEntity<JsonNode> siteResponse = restTemplate.exchange(DEIMSData.API_SITE_SEARCH + "/" + siteIdSuffix, 
				HttpMethod.GET, null, JsonNode.class);
		
		JsonNode siteNode = siteResponse.getBody();
		JsonNode attributes = siteNode.get("attributes");
		JsonNode geographic = attributes.get("geographic");
		String polygonFromApi = geographic.get("boundaries").asText();
		
		JsonNode sizeNode = geographic.get("size");
		if (sizeNode != null && !sizeNode.get("value").isNull()) {
			Double area = Double.parseDouble(sizeNode.get("value").asText());
			site.setArea(area);
		}
		
		if (polygonFromApi != null && !polygonFromApi.equals("null")) {
			boolean isMultiPolygon = polygonFromApi.contains("MULTIPOLYGON");
			boolean isMultiLineString = polygonFromApi.contains("MULTILINESTRING");
			boolean isLineString = polygonFromApi.contains("LINESTRING");
			boolean isPoint = polygonFromApi.contains("POINT");
			boolean isMultiPoint = polygonFromApi.contains("MULTIPOINT");
			
			WKTReader reader = new WKTReader();
			
			MultiPolygon multiPolygon = null;
			Polygon polygon = null;
			LineString lineString = null;
			MultiLineString multiLineString = null;
			Point point = null;
			MultiPoint multiPoint = null;
			
			try {
				if (!isMultiPolygon && !isLineString && !isMultiLineString && !isPoint && !isMultiPoint) {
					polygon = (Polygon) reader.read(polygonFromApi);
					site.setPolygon(polygon);
				} else if (isPoint) {
					point = (Point) reader.read(polygonFromApi);
					site.setPolygon(point);
				} else if (isMultiPolygon) { 
					multiPolygon = (MultiPolygon) reader.read(polygonFromApi);
					site.setPolygon(multiPolygon);
				} else if (isMultiLineString) {
					multiLineString = (MultiLineString) reader.read(polygonFromApi);
					site.setPolygon(multiLineString);
				} else if (isLineString) {
					lineString = (LineString) reader.read(polygonFromApi);
					site.setPolygon(lineString);
				}
				else {
					multiPoint = (MultiPoint) reader.read(polygonFromApi);
					site.setPolygon(multiPoint);
				}
			} catch (ParseException e) {
				throw new SimpleException(SimpleResponseDTO.PARSE_EXCEPTION);
			}
		} 
		
		return site;
	}
	
	@Override
	public List<Activity> getActivities(String siteIdSuffix) {
		
		List<Activity> activities = new ArrayList<>();
		
		ResponseEntity<JsonNode> siteResponse = restTemplate.exchange(DEIMSData.API_SITE_SEARCH + "/" + siteIdSuffix, 
				HttpMethod.GET, null, JsonNode.class);
		
		JsonNode siteNode = siteResponse.getBody();
		JsonNode attributesNode = siteNode.get("attributes");
		JsonNode focusDesignScaleNode = attributesNode.get("focusDesignScale");
		
		JsonNode researchTopicsNode = focusDesignScaleNode.get("researchTopics");
		    
	    if (researchTopicsNode != null && researchTopicsNode.isArray()) {
	    	for (JsonNode elemNode : researchTopicsNode) {     												
	    		Activity act = new Activity();
	    		act.setActivityName(elemNode.get("label").asText());
	    		
	    		activities.add(act);
	    	}
	    }
		
		return activities;
	}

	@Override
	public List<Country> getCountries(String siteIdSuffix) {
		List<Country> countries = new ArrayList<>();
		
		ResponseEntity<JsonNode> siteResponse = restTemplate.exchange(DEIMSData.API_SITE_SEARCH + "/" + siteIdSuffix, 
				HttpMethod.GET, null, JsonNode.class);
		
		JsonNode siteNode = siteResponse.getBody();
		JsonNode attributes = siteNode.get("attributes");
		JsonNode geographic = attributes.get("geographic");
		JsonNode countriesNode = geographic.get("country");
		if (countriesNode.isArray()) {
			for (JsonNode elemNode : countriesNode) {  
				Country country = new Country();
				country.setCountryName(elemNode.asText());
				
				countries.add(country);
			}
		}
		
		return countries;
	}

}
