package com.ecosense.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecosense.dto.CapabilitiesRequestDTO;
import com.ecosense.dto.DivFilterDTO;
import com.ecosense.dto.LayerDTO;
import com.ecosense.dto.DatasetLayerDTO;
import com.ecosense.dto.SimpleResponseDTO;
import com.ecosense.dto.input.FeatureInfoRequestIDTO;
import com.ecosense.dto.output.DatasetODTO;
import com.ecosense.dto.output.LayerGroupDTO;
import com.ecosense.entity.Layer;
import com.ecosense.entity.LayerGroup;
import com.ecosense.entity.LayerTime;
import com.ecosense.exception.SimpleException;
import com.ecosense.repository.LayerRepository;
import com.ecosense.repository.LayerTimeRepository;
import com.ecosense.service.DatasetService;
import com.ecosense.service.LayerService;
import com.ecosense.service.SiteService;
import com.ecosense.utils.APICallService;
import com.ecosense.utils.JsonNodeConverter;
import com.ecosense.utils.SnowcoverData;
import com.ecosense.utils.SolrData;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class LayerServiceImpl implements LayerService {
	
	@Autowired
	LayerRepository layerRepository;
	
	@Autowired
	LayerTimeRepository layerTimeRepository;

	@Autowired
	private SiteService siteService;

	@Autowired
	private DatasetService datasetService;

	private static final Logger log = LoggerFactory.getLogger(LayerServiceImpl.class);
	
	@Autowired
	private @Qualifier("nonSSLRestTemplate") RestTemplate restTemplateNonSSL;

	// @Autowired
	// private RestTemplate restTemplate;

	@Autowired
	private APICallService apiCallService;
	
	private final Double LAT_LNG_RADIUS = 0.005;

	@Override
	public List<LayerDTO> getAll(List<String> layertypes, List<Integer> ids, String code) throws SimpleException {
		List<LayerDTO> layers = new ArrayList<>();
		List<Layer> layersFromDB = null;
		
		layersFromDB = layerRepository.getAllActive(layertypes, code, ids);
		
		for (Layer layer : layersFromDB) {
			layers.add(new LayerDTO(layer, true, true));
		}
		return layers;
	}
	
	@Override
	public String getFeatureInfoHTML(FeatureInfoRequestIDTO firDB) throws Exception {
		Layer layer = layerRepository.findLayerByCode(firDB.getType());
		Double lat = firDB.getLatLng().getLat();
		Double lng = firDB.getLatLng().getLng();

		String queryLayers = "QUERY_LAYERS=" + layer.getLayerName();
		String layers = "LAYERS=" + layer.getLayerName();
		String bbox = "BBOX=" + (lng - LAT_LNG_RADIUS) + "," + (lat - LAT_LNG_RADIUS) + "," + (lng + LAT_LNG_RADIUS) + "," + (lat + LAT_LNG_RADIUS);
		String url = layer.getGeoUrlWfs() + 
				"?service=WMS&VERSION=" + layer.getVersion() + "&request=GetFeatureInfo&crs=CRS:84&WIDTH=825&HEIGHT=842&X=50&Y=50&INFO_FORMAT=text/html&FORMAT=image/png&" + 
				layers + "&" + queryLayers + "&" + bbox;
		
		
		System.out.println(url);
		ResponseEntity<String> response = restTemplateNonSSL.exchange(url, HttpMethod.GET, null, String.class);
		Document doc = Jsoup.parse(response.getBody());
		
		return doc.toString();
	}

	@Override
	public Boolean addTimeForSnowCover() throws SimpleException {
		System.out.println(" addTimeForSnowCover start");
		String url = SnowcoverData.ELTER_URL + SnowcoverData.GET_CAPABILITIES;
		
		System.out.println(url);
		
		ResponseEntity<String> response = restTemplateNonSSL.exchange(url, HttpMethod.GET, null, String.class);
		Document doc = Jsoup.parse(response.getBody());
		
		Elements capability = doc.getElementsByTag("Capability");
		Elements layerTag = capability.get(0).getElementsByTag("Layer");
		
		Elements layers = layerTag.get(0).getElementsByTag("layer");
		
		layers.forEach( layerEl -> {
			Elements layerNameEl = layerEl.getElementsByTag("Name");
			
			String layerName = layerNameEl.get(0).ownText();
			
			Layer layerFromDB = layerRepository.findActiveLayerByCode(layerName);
			
			if (layerFromDB != null) {
				Elements dimensionEl = layerEl.getElementsByTag("Dimension");
				
				if (!dimensionEl.isEmpty()) {
					String[] times = dimensionEl.get(0).ownText().split(",");
					
					for(int i=0; i < times.length; i++) {
						String date = times[i].trim();
						List<LayerTime> layerTimes = layerTimeRepository.findByNameAndTime(date, layerName);
						
						if (layerTimes.isEmpty()) {
							LayerTime layerTime = new LayerTime();
							layerTime.setAvailableTime(date);
							layerTime.setLayer(layerFromDB);
							
							layerTimeRepository.save(layerTime);
							
							System.out.println("New LayerTime with ID " + layerTime.getId());
						}
					}
				}
			}
			
			
		});

		System.out.println(" addTimeForSnowCover end");
		return true;
	}

	@Override
	public List<LayerGroupDTO> getLayerGroups(List<String> layertype, Integer groupdId) throws SimpleException {
		List<LayerGroupDTO> dtos = new ArrayList<>();
		List<LayerGroup> layerGroups = null;
				
		if (layertype != null) {
			layerGroups = layerRepository.getLayerGroupActive(layertype);
		} else if (groupdId != null) {
			layerGroups = layerRepository.getByLayerGroupId(groupdId);
		}
		
		for(LayerGroup lg : layerGroups) {
			dtos.add(new LayerGroupDTO(lg, true));
		}
		
		return dtos;
	}

	@Override
	public String getCapabilities(CapabilitiesRequestDTO capabilitiesRequestDTO) {
		ResponseEntity<String> response = restTemplateNonSSL.exchange(capabilitiesRequestDTO.getUrl(), HttpMethod.GET, null, String.class);
		Document doc = Jsoup.parse(response.getBody());
		
		String xml = doc.toString();
		xml = xml.replace("\n", "").replace("\r", "");
		xml = xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		
		return xml;
	}

	public List<DatasetLayerDTO> filterAndSearch(DivFilterDTO divFilterDTO) throws SimpleException{
		Set<DatasetLayerDTO> datasetLayerDTOs = new HashSet<>();

		String url = SolrData.BASE_URL + "/select?q=json_dataset:(";

		String filterUrl = siteService.createUrlFromDivFilter(divFilterDTO);
		url += filterUrl;

		url += ")";
		url += "&rows=1000&start=0";

		JsonNode resultNode = apiCallService.getRequest(url);
		JsonNode datasetLayerListNode = resultNode.get("response").get("docs");

		for (JsonNode datasetLayerNode : datasetLayerListNode) {
			try {
				DatasetLayerDTO datasetLayerDTO = createDatasetLayerDTO(datasetLayerNode);
				datasetLayerDTOs.add(datasetLayerDTO);
			} catch (Exception e) {
				continue;
			}
		}

		return new ArrayList<>(datasetLayerDTOs);
	}

	private DatasetLayerDTO createDatasetLayerDTO(JsonNode datasetLayerNode) throws SimpleException {
		DatasetLayerDTO datasetLayerDTO = new DatasetLayerDTO();

		Integer layerId = datasetLayerNode.get("id").asInt();
		String jsonDataset = datasetLayerNode.get("json_dataset").asText();

		Layer layer = layerRepository.findById(layerId).orElse(null);
		if (layer != null) {
			LayerDTO layerDTO = new LayerDTO();
			layerDTO.setId(layerId);
			layerDTO.setLayerType(layer.getLayerType());
			layerDTO.setName(layer.getName());
			layerDTO.setUuid(layer.getUuid());

			datasetLayerDTO.setLayer(layerDTO);
		}

		JsonNode jsonDatasetNode = null;
		
		try {
			jsonDatasetNode = JsonNodeConverter.convertStringToJsonNode(jsonDataset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SimpleException(SimpleResponseDTO.GENERAL_API_ERROR);
		}

		DatasetODTO datasetODTO = datasetService.parseDatasetFromNode(jsonDatasetNode, false);
		
		datasetLayerDTO.setDataset(datasetODTO);

		return datasetLayerDTO;
		
	}

	public List<LayerGroupDTO> searchLayerGroupsByDatasetData(String search, String layerType) throws SimpleException {
		List<LayerGroupDTO> layerGroupDTOs = new ArrayList<>();

		String url = SolrData.BASE_URL + "/select?q=json_dataset:(" + '"' + search + '"' + ")";
		url += "&rows=1000&start=0";

		JsonNode resultNode = apiCallService.getRequest(url);
		JsonNode datasetLayerListNode = resultNode.get("response").get("docs");

		for (JsonNode datasetLayerNode : datasetLayerListNode) {
			try {
				Integer layerId = datasetLayerNode.get("id").asInt();


				Layer layer = layerRepository.findById(layerId).orElse(null);
				if (layer != null && layer.getLayerGroup() != null && layer.getLayerType().equals(layerType)) {
					LayerGroupDTO layerGroupDTO = new LayerGroupDTO(layer.getLayerGroup());
					if (!layerGroupDTOs.contains(layerGroupDTO)) {
						layerGroupDTO.setLayers(new ArrayList<>());
						layerGroupDTOs.add(layerGroupDTO);
					} else {
						layerGroupDTO = layerGroupDTOs.get(layerGroupDTOs.indexOf(layerGroupDTO));
					}

					LayerDTO layerDTO = new LayerDTO(layer);

					if (!layerGroupDTO.getLayers().contains(layerDTO)) {
						layerGroupDTO.getLayers().add(new LayerDTO(layer));
					}
				}

			} catch (Exception e) {
				continue;
			}
		}

		return layerGroupDTOs;
	}

}

