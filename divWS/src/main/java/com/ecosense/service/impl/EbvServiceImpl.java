package com.ecosense.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.EbvClassDTO;
import com.ecosense.dto.EbvCreatorDetailNodeDTO;
import com.ecosense.dto.EbvDTO;
import com.ecosense.dto.EbvDatasetDetailNodeDTO;
import com.ecosense.dto.EbvDetailDTO;
import com.ecosense.dto.EbvDetailNodeDTO;
import com.ecosense.dto.EbvEntityDetailNodeDTO;
import com.ecosense.dto.EbvEntityTypeDTO;
import com.ecosense.dto.EbvFileDetailNodeDTO;
import com.ecosense.dto.EbvMetricDTO;
import com.ecosense.dto.EbvMetricsDetailNodeDTO;
import com.ecosense.dto.EbvNameDTO;
import com.ecosense.dto.EbvPublisherDetailNodeDTO;
import com.ecosense.dto.EbvSpatialDetailNodeDTO;
import com.ecosense.dto.EbvSpatialScopeDTO;
import com.ecosense.dto.EbvTimeCoverageDetailNodeDTO;
import com.ecosense.dto.input.EbvFilterIDTO;
import com.ecosense.entity.Ebv;
import com.ecosense.entity.EbvClass;
import com.ecosense.entity.EbvEntityType;
import com.ecosense.entity.EbvName;
import com.ecosense.entity.EbvSpatialScope;
import com.ecosense.repository.EbvClassRepository;
import com.ecosense.repository.EbvEntityTypeRepository;
import com.ecosense.repository.EbvEnvironmentalDomainRepository;
import com.ecosense.repository.EbvNameRepository;
import com.ecosense.repository.EbvRepository;
import com.ecosense.repository.EbvSpatialScopeRepository;
import com.ecosense.repository.EbvTemporalResolutionRepository;
import com.ecosense.service.EbvService;
import com.ecosense.utils.EbvData;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class EbvServiceImpl implements EbvService {
	
	@Autowired private EbvNameRepository ebvNameRepository;
	@Autowired private EbvTemporalResolutionRepository ebvTemporalResolutionRepository;
	@Autowired private EbvEntityTypeRepository ebvEntityTypeRepository;
	@Autowired private EbvSpatialScopeRepository ebvSpatialScopeRepository;
	@Autowired private EbvEnvironmentalDomainRepository ebvEnvironmentalDomainRepository;
	@Autowired private EbvClassRepository ebvClassRepository;
	@Autowired private EbvRepository ebvRepository;
	
	@Autowired private RestTemplate restTemplate;
	
	@Override
	public void refreshEbv() {
		ResponseEntity<JsonNode> response = restTemplate.exchange(EbvData.URL_EBV, HttpMethod.GET, null, JsonNode.class);
		JsonNode body = response.getBody();
		
		for(JsonNode data : body.get("data")) {
			String ebvClass = data.get("ebv_class").asText();
			Optional<EbvClass> ebvClassOpt = ebvClassRepository.findEbvClassByName(ebvClass);
			EbvClass ebvClassEntity = null;
			if (!ebvClassOpt.isPresent()) {
				ebvClassEntity = new EbvClass();
				ebvClassEntity.setName(ebvClass);
				ebvClassRepository.save(ebvClassEntity);
				ebvClassRepository.flush();
			} else {
				ebvClassEntity = ebvClassOpt.get();
			}
			
			for(JsonNode ebvName : data.get("ebv_name")) {
				String ebvNameTxt = ebvName.asText();
				Optional<EbvName> ebvNameOpt = ebvNameRepository.findEbvNameByName(ebvNameTxt);
				EbvName ebvNameEntity = null;
				if (!ebvNameOpt.isPresent()) {
					ebvNameEntity = new EbvName();
					ebvNameEntity.setName(ebvNameTxt);
				} else {
					ebvNameEntity = ebvNameOpt.get();
				}
				
				ebvNameEntity.setEbvClass(ebvClassEntity);
				ebvNameRepository.save(ebvNameEntity);
				ebvNameRepository.flush();
			}
		}
	}

	@Override
	public void refreshDataset() {
		System.out.println(" ---- start of refresh ebv ----");
		this.refreshEbv();
		ResponseEntity<JsonNode> response = restTemplate.exchange(EbvData.URL_DATASET, HttpMethod.GET, null, JsonNode.class);
		
		JsonNode body = response.getBody();
		
		for(JsonNode data : body.get("data")) {
			Ebv ebv = new Ebv();
			Integer ebvId = Integer.parseInt(data.get("id").asText());
			Optional<Ebv> ebvOpt = ebvRepository.findEbvByEbvId(ebvId);
			if (ebvOpt.isPresent()) {
				ebv = ebvOpt.get();
			}
			ebv.setEbvId(ebvId);
			
			String title = data.get("title").isNull() ? null : data.get("title").asText();
			ebv.setTitle(title);
			
			if (!data.get("creator").isNull()) {
				String creatorName = data.get("creator").get("creator_name").asText();
				ebv.setCreatorName(creatorName);
			}
			
			String dateCreated = data.get("date_created").asText();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date parsedDate;
			try {
				parsedDate = dateFormat.parse(dateCreated);
				ebv.setDateCreated(parsedDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			JsonNode ebvNode = data.get("ebv");
			String ebvClass = ebvNode.get("ebv_class").asText();
			Optional<EbvClass> ebvClassOpt = ebvClassRepository.findEbvClassByName(ebvClass);
			EbvClass ebvClassEntity = ebvClassOpt.get();
			ebv.setEbvClass(ebvClassEntity);
			
			String ebvName = ebvNode.get("ebv_name").asText();
			Optional<EbvName> ebvNameOpt = ebvNameRepository.findEbvNameByName(ebvName);
			EbvName ebvNameEntity = ebvNameOpt.get();
			ebv.setEbvName(ebvNameEntity);
			
			if (!data.get("ebv_entity").isNull()) {
				String ebvEntityType = data.get("ebv_entity").get("ebv_entity_type").asText();
				Optional<EbvEntityType> ebvEntityTypeOpt = ebvEntityTypeRepository.findEbvEntityTypeByName(ebvEntityType);
				EbvEntityType ebvEntityTypeEntity = null;
				if (!ebvEntityTypeOpt.isPresent()) {
					ebvEntityTypeEntity = new EbvEntityType();
					ebvEntityTypeEntity.setName(ebvEntityType);
					ebvEntityTypeRepository.save(ebvEntityTypeEntity);
					ebvEntityTypeRepository.flush();
				} else {
					ebvEntityTypeEntity = ebvEntityTypeOpt.get();
				}
				ebv.setEbvEntityType(ebvEntityTypeEntity);
			}
			
			if (data.get("ebv_spatial") != null && data.get("ebv_spatial") != null && data.get("ebv_spatial").get("ebv_spatial_scope") != null) {
				String ebvSpatialScope = data.get("ebv_spatial").get("ebv_spatial_scope").asText();
				Optional<EbvSpatialScope> ebvSpatialScopeOpt = ebvSpatialScopeRepository.findEbvSpatialScopeByName(ebvSpatialScope);
				EbvSpatialScope ebvSpatialScopeEntity = null;
				if (!ebvSpatialScopeOpt.isPresent()) {
					ebvSpatialScopeEntity = new EbvSpatialScope();
					ebvSpatialScopeEntity.setName(ebvSpatialScope);
					ebvSpatialScopeRepository.save(ebvSpatialScopeEntity);
					ebvSpatialScopeRepository.flush();
				} else {
					ebvSpatialScopeEntity = ebvSpatialScopeOpt.get();
				}
				ebv.setEbvSpatialScope(ebvSpatialScopeEntity);
			}
			
			
			ebvRepository.save(ebv);
		}
		
		System.out.println(" ---- end of refresh ebv ----");
	}

	@Override
	public List<EbvClassDTO> getAllEbvClass() {
		List<EbvClassDTO> res = new LinkedList<>();
		
		List<EbvClass> fromDB = ebvClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
		for(EbvClass entity: fromDB) {
			EbvClassDTO entityDB = new EbvClassDTO(entity);
			res.add(entityDB);
		}
		
		return res;
	}

	@Override
	public List<EbvNameDTO> getAllEbvName() {
		List<EbvNameDTO> res = new LinkedList<>();
		
		List<EbvName> fromDB = ebvNameRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
		for(EbvName entity: fromDB) {
			EbvNameDTO entityDB = new EbvNameDTO(entity);
			res.add(entityDB);
		}
		
		return res;
	}

	@Override
	public List<EbvEntityTypeDTO> getAllEbvEntityType() {
		List<EbvEntityTypeDTO> res = new LinkedList<>();
		
		List<EbvEntityType> fromDB = ebvEntityTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
		for(EbvEntityType entity: fromDB) {
			EbvEntityTypeDTO entityDB = new EbvEntityTypeDTO(entity);
			res.add(entityDB);
		}
		
		return res;
	}

	@Override
	public List<EbvSpatialScopeDTO> getAllEbvSpatialScope() {
	List<EbvSpatialScopeDTO> res = new LinkedList<>();
		
		List<EbvSpatialScope> fromDB = ebvSpatialScopeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
		for(EbvSpatialScope entity: fromDB) {
			EbvSpatialScopeDTO entityDB = new EbvSpatialScopeDTO(entity);
			res.add(entityDB);
		}
		
		return res;
	}

	@Override
	public EbvDetailDTO getDetail(Integer id) {
		String url = EbvData.URL_DATASET + "/" + id;
		ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, null, JsonNode.class);
		EbvDetailDTO detailDTO = new EbvDetailDTO();
		
		JsonNode body = response.getBody();
		JsonNode dataNode = body.get("data").get(0);
		System.out.println(dataNode.toPrettyString());
		detailDTO.setId(Integer.parseInt(dataNode.get("id").asText()));
		detailDTO.setNamingAuthority(dataNode.get("naming_authority").asText());
		detailDTO.setTitle(dataNode.get("title").asText());
		detailDTO.setDateCreated(dataNode.get("date_created").asText());
		detailDTO.setSummary(dataNode.get("summary").asText());
		
		List<String> references = new ArrayList<>();
		for(JsonNode referenceNode : dataNode.get("references")) {
			references.add(referenceNode.asText());
		}
		detailDTO.setReferences(references);
		

		detailDTO.setSource(dataNode.get("source") == null ? null : dataNode.get("source").asText());
		detailDTO.setCoverageContentType(dataNode.get("coverage_content_type") == null ? null : dataNode.get("coverage_content_type").asText());
		detailDTO.setProcessingLevel(dataNode.get("processing_level") == null ? null : dataNode.get("processing_level").asText());
		detailDTO.setProject(dataNode.get("project") == null ? null : dataNode.get("project").asText());
		
		List<String> projectUrls = new ArrayList<>();
		for(JsonNode projectUrlNode : dataNode.get("project_url")) {
			projectUrls.add(projectUrlNode.asText());
		}
		detailDTO.setProjectUrl(projectUrls);
		
		JsonNode creatorNode = dataNode.get("creator");
		EbvCreatorDetailNodeDTO ebvCreatorDTO = new EbvCreatorDetailNodeDTO(creatorNode.get("creator_name").asText(), creatorNode.get("creator_email").asText(), 
																			creatorNode.get("creator_institution").asText(), creatorNode.get("creator_url").asText());
		detailDTO.setCreator(ebvCreatorDTO);
		
		detailDTO.setContributorName(dataNode.get("contributor_name").asText());
		detailDTO.setLicense(dataNode.get("license").asText());
		
		JsonNode publisherNode = dataNode.get("publisher");
		if (publisherNode != null) {
			EbvPublisherDetailNodeDTO ebvPublisherDTO = new EbvPublisherDetailNodeDTO(publisherNode.get("publisher_name").asText(), publisherNode.get("publisher_email").asText(), 
					publisherNode.get("publisher_institution").asText(), publisherNode.get("publisher_url").asText());
			detailDTO.setPublisher(ebvPublisherDTO);
		}
		
		JsonNode ebvNode = dataNode.get("ebv");
		if (ebvNode != null) {
			EbvDetailNodeDTO ebvDTO = new EbvDetailNodeDTO(chekIsNodeNull(ebvNode.get("ebv_class")) ? null : ebvNode.get("ebv_class").asText(), 
					chekIsNodeNull(ebvNode.get("ebv_name")) ? null : ebvNode.get("ebv_name").asText());
			detailDTO.setEbv(ebvDTO);
		}
		
		JsonNode ebvEntityNode = dataNode.get("ebv_entity");
		if (ebvEntityNode != null) {
			EbvEntityDetailNodeDTO ebvEntityDTO = new EbvEntityDetailNodeDTO(chekIsNodeNull(ebvEntityNode.get("ebv_entity_type")) ? null : ebvEntityNode.get("ebv_entity_type").asText(), 
					chekIsNodeNull(ebvEntityNode.get("ebv_entity_scope")) ? null : ebvEntityNode.get("ebv_entity_scope").asText(), 
					chekIsNodeNull(ebvEntityNode.get("ebv_entity_classification_name")) ? null : ebvEntityNode.get("ebv_entity_classification_name").asText(), 
					chekIsNodeNull(ebvEntityNode.get("ebv_entity_classification_url")) ? null : ebvEntityNode.get("ebv_entity_classification_url").asText());
			detailDTO.setEbvEntity(ebvEntityDTO);
		}
		
		JsonNode ebvMetricNode = dataNode.get("ebv_metric");
		EbvMetricDTO metric0 = null;
		if (!ebvMetricNode.isNull() && ebvMetricNode.get("metric0") != null) {
			JsonNode metric0Node = ebvMetricNode.get("metric0");
			if (metric0Node != null) {
			metric0 = new EbvMetricDTO(chekIsNodeNull(metric0Node.get("name")) ? null : metric0Node.get("name").asText(), 
					chekIsNodeNull(metric0Node.get("description")) ? null :metric0Node.get("description").asText(), 
					chekIsNodeNull(metric0Node.get("unit")) ? null :metric0Node.get("unit").asText());
			}
		}
		EbvMetricDTO metric1 = null;
		if (!ebvMetricNode.isNull() && ebvMetricNode.get("metric1") != null) {
			JsonNode metric1Node = ebvMetricNode.get("metric1");
			metric1 = new EbvMetricDTO(metric1Node.get("name").asText(), metric1Node.get("description").asText(), metric1Node.get("unit").asText());
		}
		EbvMetricsDetailNodeDTO ebvMetricsDTO = new EbvMetricsDetailNodeDTO(metric0, metric1);
		detailDTO.setEbvMetric(ebvMetricsDTO);
		
		JsonNode ebvSpatialNode = dataNode.get("ebv_geospatial");
		if (ebvSpatialNode != null) {
			EbvSpatialDetailNodeDTO ebvSpatialDTO = new EbvSpatialDetailNodeDTO(chekIsNodeNull(ebvSpatialNode.get("ebv_geospatial_scope"))  ? null : ebvSpatialNode.get("ebv_geospatial_scope").asText(), 
					chekIsNodeNull(ebvSpatialNode.get("ebv_geospatial_description")) ? null : ebvSpatialNode.get("ebv_geospatial_description").asText());
			detailDTO.setEbvSpatial(ebvSpatialDTO);
		}
		
		detailDTO.setGeospatialLatUnits(chekIsNodeNull(dataNode.get("geospatial_lat_units")) ? null : dataNode.get("geospatial_lat_units").asText());
		detailDTO.setGeospatialLonUnits(chekIsNodeNull(dataNode.get("geospatial_lon_units")) ? null : dataNode.get("geospatial_lon_units").asText());
		detailDTO.setEbvDomain(chekIsNodeNull(dataNode.get("ebv_domain")) ? null : dataNode.get("ebv_domain").asText());
		detailDTO.setComment(chekIsNodeNull(dataNode.get("comment")) ? null : dataNode.get("comment").asText());
		
		JsonNode ebvTimeCoverageNode = dataNode.get("time_coverage");
		if (ebvTimeCoverageNode != null) {
			EbvTimeCoverageDetailNodeDTO ebvTimeCoverageDTO = new EbvTimeCoverageDetailNodeDTO(
					chekIsNodeNull(ebvTimeCoverageNode.get("time_coverage_resolution")) ? null : ebvTimeCoverageNode.get("time_coverage_resolution").asText(), 
					chekIsNodeNull(ebvTimeCoverageNode.get("time_coverage_start")) ? null : ebvTimeCoverageNode.get("time_coverage_start").asText(), 
					chekIsNodeNull(ebvTimeCoverageNode.get("time_coverage_end")) ? null : ebvTimeCoverageNode.get("time_coverage_end").asText());
			detailDTO.setTimeCoverage(ebvTimeCoverageDTO);
		}
		
		JsonNode ebvDatasetNode = dataNode.get("dataset");
		if (ebvDatasetNode != null) {
			EbvDatasetDetailNodeDTO ebvDatasetDTO = new EbvDatasetDetailNodeDTO(ebvDatasetNode.get("pathname") == null? null : ebvDatasetNode.get("pathname").asText(), 
					chekIsNodeNull(ebvDatasetNode.get("download")) ? null :ebvDatasetNode.get("download").asText(), 
					chekIsNodeNull(ebvDatasetNode.get("metadata_json")) ? null : ebvDatasetNode.get("metadata_json").asText(), 
					chekIsNodeNull(ebvDatasetNode.get("metadata_xml")) ? null :	ebvDatasetNode.get("metadata_xml").asText());
			detailDTO.setDataset(ebvDatasetDTO);
		}
		
		JsonNode ebvFileNode = dataNode.get("file");
		if (ebvFileNode != null ) {
			EbvFileDetailNodeDTO ebvFileDTO = new EbvFileDetailNodeDTO(chekIsNodeNull(ebvFileNode.get("download")) ? null : ebvFileNode.get("download").asText());
			detailDTO.setFile(ebvFileDTO);
		}
		
		return detailDTO;
	}
	
	private Boolean chekIsNodeNull(JsonNode node) {
		if (node == null || node.isNull()) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public List<EbvDTO> filterEbv(EbvFilterIDTO ebvFilter) {
		List<EbvDTO> response = ebvRepository.filterEbv(ebvFilter);
		
		return response;
	}

	@Override
	public List<String> getAllCreators() {
		return ebvRepository.findAllCreators();
	}

	@Override
	public List<EbvNameDTO> getEbvName(Integer ebvClassId) {
		List<EbvNameDTO> res = new LinkedList<>();
		
		List<EbvName> fromDB = ebvNameRepository.findByEbvClass(ebvClassId);
		for(EbvName entity: fromDB) {
			EbvNameDTO entityDB = new EbvNameDTO(entity);
			res.add(entityDB);
		}
		
		return res;
	}

}
